package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "prompt_history")
public class PromptHistory {

    @EmbeddedId
    private ApiKey apiKey;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private Date createdAt = new java.util.Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_api_key", nullable = false)
    private Project project;
}
