package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.persistance.UserRepository;
import at.kalwoda.nocodeapi.service.UserService;
import at.kalwoda.nocodeapi.service.commands.UserCommands;
import at.kalwoda.nocodeapi.service.dtos.user.UserDto;
import at.kalwoda.nocodeapi.service.dtos.user.UserMinimalDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiConstants.API + "/users")
public class UserRestController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        log.info("Fetching user details for user: {}", authentication.getName());
        return ResponseEntity.ok(new UserDto(userService.getUser(authentication.getName())));
    }

    @GetMapping("/minimal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserMinimalDto> getUserMinimal(Authentication authentication) {
        log.info("Fetching minimal user details for user: {}", authentication.getName());
        return userService.getMinimal(authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
