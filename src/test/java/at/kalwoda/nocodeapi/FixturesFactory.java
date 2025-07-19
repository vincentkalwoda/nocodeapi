package at.kalwoda.nocodeapi;

import at.kalwoda.nocodeapi.domain.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class FixturesFactory {
    public static Username username1() {
        return new Username("testuser");
    }

    public static Username username2() {
        return new Username("testuser1");
    }

    public static Email email1() {
        return new Email("testuser@mail.com");
    }

    public static Email email2() {
        return new Email("testuser1@mail.com");
    }

    public static User user() {
        return User.builder()
                .apiKey(new ApiKey("user-api-key"))
                .username(username1())
                .email(email1())
                .password("password")
                .role(UserRole.USER)
                .createdAt(new Date())
                .lastLogin(new Date())
                .isActive(true)
                .isEmailVerified(true)
                .refreshToken("token")
                .refreshTokenExpiresAt(LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(1, 0, 0, 0)))
                .build();
    }

    public static User admin() {
        return User.builder()
                .apiKey(new ApiKey("admin-api-key"))
                .username(username2())
                .email(email2())
                .password("adminpassword")
                .role(UserRole.ADMIN)
                .createdAt(new Date())
                .lastLogin(new Date())
                .isActive(true)
                .isEmailVerified(true)
                .refreshToken("admintoken")
                .refreshTokenExpiresAt(LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(1, 0, 0, 0)))
                .build();
    }

    public static Project project() {
        return Project.builder()
                .apiKey(new ApiKey("project-api-key"))
                .name("Test Project")
                .description("This is a test project")
                .createdAt(new Date())
                .user(user())
                .build();
    }

    public static EntityModel entityModel(Project project) {
        return EntityModel.builder()
                .apiKey(new ApiKey("entity-model-api-key"))
                .name("Test Entity")
                .project(project)
                .build();
    }


}
