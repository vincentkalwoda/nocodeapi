package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "api_access_tokens")
public class ApiAccessToken {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "api_key", nullable = false, unique = true))
    private ApiKey apiKey;

    @Column(name = "created_at", nullable = false)
    private Date createdAt = new Date();

    @Column(name = "expires_at", nullable = false)
    private Date expiresAt = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);

    @OneToOne
    @JoinColumn(name = "project_api_key", nullable = false) // 🔥 now distinct from PK
    private Project project;
}

