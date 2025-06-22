package at.kalwoda.nocodeapi.service.dtos.user;

import at.kalwoda.nocodeapi.domain.User;

public record UserMinimalDto(
        String apiKey,
        String username,
        String email,
        String role,
        boolean isActive
) {
    public UserMinimalDto(User user) {
        this(
                user.getApiKey().value(),
                user.getUsername().value(),
                user.getEmail().value(),
                user.getRole().name(),
                user.getIsActive()
        );
    }
}
