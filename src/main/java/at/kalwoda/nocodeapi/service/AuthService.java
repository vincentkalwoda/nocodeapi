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
        try {
            log.info("Attempting to authenticate user with username={}", command.username());

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(command.username(), command.password())
            );

            log.info("User authenticated successfully: {}", command.username());

            String accessToken = tokenService.generateAccessToken(auth);

            String refreshToken = tokenService.generateRefreshToken(auth);

            log.info("Generated tokens for user: {}", command.username());

            User user = userRepository.findByUsername(new Username(command.username()))
                    .orElseThrow(() -> new IllegalStateException("Authenticated but user not found"));

            log.info("User found in repository: {}", user.getUsername().value());

            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiresAt(LocalDateTime.now().plusMonths(3));
            user.setLastLogin(new Date());

            userRepository.save(user);

            return new LoginResult(accessToken, refreshToken);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
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
        log.info("Creating user with command={}", command);
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
}