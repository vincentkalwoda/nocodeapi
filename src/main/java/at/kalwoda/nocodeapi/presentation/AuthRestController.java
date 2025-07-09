package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.service.AuthService;
import at.kalwoda.nocodeapi.service.UserService;
import at.kalwoda.nocodeapi.service.commands.UserCommands;
import at.kalwoda.nocodeapi.service.dtos.user.LoginResult;
import at.kalwoda.nocodeapi.service.dtos.user.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
@RequestMapping(ApiConstants.API)
public class AuthRestController {
    private final AuthService authService;

    @GetMapping("/validate")
    public ResponseEntity<Map<String, String>> validateUser(Authentication authentication) {
        String role = authService.isLoggedIn(authentication);
        return ResponseEntity.ok(Map.of("role", role));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody UserCommands.LoginCommand command) {
        log.info("Attempting login for user: {}", command.username());

        var result = authService.login(command);

        log.info("Logged in user: {}", result);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", result.accessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60)
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", result.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60L * 60 * 24 * 90)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString(), refreshTokenCookie.toString())
                .body(result);

    }

    @PostMapping("register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserCommands.RegisterCommand command) {
        log.info("Creating user with username: {}", command.username());
        var createdUser = authService.register(command);
        log.info("User created successfully with API key: {}", createdUser.getApiKey().value());
        return ResponseEntity.status(201).body(new UserDto(createdUser));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, Authentication authentication) {
        authService.logout(authentication.getName());

        Cookie cookie = new Cookie("accessToken", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
