package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

@Entity
@Table(name = "users")
public class User {
    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "api_key", nullable = false, unique = true))
    ApiKey apiKey;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "username", nullable = false, unique = true))
    @NotBlank(message = "Username is required")
    Username username;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false, unique = true))
    @NotBlank(message = "Email is required")
    Email email;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is required")
    String password;

    @Column(name = "role", nullable = false, length = 1)
    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    UserRole role = UserRole.USER;

    @Column(name = "created_at", nullable = false)
    @NotNull(message = "Creation date is required")
    Date createdAt = new Date();

    @Column(name = "last_login", nullable = true)
    Date lastLogin;

    @Column(name = "is_active", nullable = false)
    @NotNull(message = "Active status is required")
    Boolean isActive = true;

    @Column(name = "is_email_verified", nullable = false)
    @NotNull(message = "Email verification status is required")
    Boolean isEmailVerified = false;

    @Column(name = "session_token", nullable = true)
    String sessionToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Project> projects;
}
