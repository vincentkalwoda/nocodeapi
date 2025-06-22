package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.service.AuthService;
import at.kalwoda.nocodeapi.service.UserService;
import at.kalwoda.nocodeapi.service.commands.UserCommands;
import at.kalwoda.nocodeapi.service.dtos.user.LoginResult;
import at.kalwoda.nocodeapi.service.dtos.user.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
@RequestMapping(ApiConstants.API)
public class AuthRestController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody UserCommands.LoginCommand command) {
        log.info("Attempting login for user: {}", command.username());

        var result = authService.login(command);

        log.info("Logged in user: {}", result);

        return ResponseEntity.ok(result);
    }

    @PostMapping("register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserCommands.RegisterCommand command) {
        log.info("Creating user with username: {}", command.username());
        var createdUser = authService.register(command);
        log.info("User created successfully with API key: {}", createdUser.getApiKey().value());
        return ResponseEntity.status(201).body(new UserDto(createdUser));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        log.info("Logging out user: {}", authentication.getName());
        authService.logout(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
