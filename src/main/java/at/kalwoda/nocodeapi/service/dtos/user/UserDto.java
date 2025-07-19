package at.kalwoda.nocodeapi.service.dtos.user;

import at.kalwoda.nocodeapi.domain.User;
import at.kalwoda.nocodeapi.domain.UserRole;

import java.time.LocalDateTime;
import java.util.Date;

public record UserDto(
        String apiKey,
        String username,
        String email,
        UserRole role,
        Date createdAt,
        Date lastLogin,
        Boolean isActive,
        Boolean isEmailVerified,
        String refreshToken,
        LocalDateTime refreshTokenExpiresAt
) {
    public UserDto(User user) {
        this(
                user.getApiKey().value(),
                user.getUsername().value(),
                user.getEmail().value(),
                user.getRole(),
                user.getCreatedAt(),
                user.getLastLogin(),
                user.getIsActive(),
                user.getIsEmailVerified(),
                user.getRefreshToken(),
                user.getRefreshTokenExpiresAt()
        );
    }
}
