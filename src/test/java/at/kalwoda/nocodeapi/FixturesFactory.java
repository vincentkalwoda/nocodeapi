package at.kalwoda.nocodeapi;

import at.kalwoda.nocodeapi.domain.*;

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
                .sessionToken("token")
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
                .sessionToken("admintoken")
                .build();
    }
}
