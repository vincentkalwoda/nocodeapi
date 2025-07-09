package at.kalwoda.nocodeapi.service;

import at.kalwoda.nocodeapi.domain.*;
import at.kalwoda.nocodeapi.foundation.Base58;
import at.kalwoda.nocodeapi.persistance.UserRepository;
import at.kalwoda.nocodeapi.service.commands.UserCommands;
import at.kalwoda.nocodeapi.service.commands.UserCommands.LoginCommand;
import at.kalwoda.nocodeapi.service.dtos.user.LoginResult;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginResult login(@Valid LoginCommand command) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.username(), command.password())
        );

        String accessToken = tokenService.generateAccessToken(auth);

        String refreshToken = tokenService.generateRefreshToken(auth);

        User user = userRepository.findByUsername(new Username(command.username()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!user.getIsActive())
            throw new BadCredentialsException("Your account has been deactivated. Please contact support.");
        if (!user.getIsEmailVerified())
            throw new BadCredentialsException("Please verify your email address to log in.");


        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiresAt(LocalDateTime.now().plusMonths(3));
        user.setLastLogin(new Date());

        userRepository.save(user);

        return new LoginResult(accessToken, refreshToken);

    }

    public LoginResult refreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (user.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getUsername().value(), null,
                List.of(() -> user.getRole().name())
        );

        String newAccessToken = tokenService.generateAccessToken(auth);

        String newRefreshToken = tokenService.generateRefreshToken(auth);
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiresAt(LocalDateTime.now().plusMonths(3));

        userRepository.save(user);

        return new LoginResult(newAccessToken, newRefreshToken);
    }

    public void logout(String username) {
        userRepository.findByUsername(new Username(username))
                .ifPresent(user -> {
                    user.setRefreshToken(null);
                    user.setRefreshTokenExpiresAt(null);
                    userRepository.save(user);
                });
    }

    public User register(@Valid UserCommands.RegisterCommand command) {
        userRepository.findByUsername(new Username(command.username()))
                .ifPresent(user -> {
                    throw new IllegalStateException("Username already exists");
                });
        userRepository.findByEmail(new Email(command.email()))
                .ifPresent(user -> {
                    throw new IllegalStateException("Email already exists");
                });

        if (!command.password().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$")) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.");
        }

        ApiKey apiKey;
        do {
            apiKey = new ApiKey("u_" + Base58.random(16));
        } while (userRepository.findByApiKey(apiKey).isPresent());

        var user = User.builder()
                .apiKey(apiKey)
                .username(new Username(command.username()))
                .email(new Email(command.email()))
                .password(passwordEncoder.encode(command.password()))
                .role(UserRole.USER)
                .isActive(true)
                .createdAt(new Date())
                .isEmailVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created with apiKey={}", savedUser.getApiKey().value());
        return savedUser;
    }

    public String isLoggedIn(Authentication authentication) {
        return tokenService.getRole(authentication);
    }
}