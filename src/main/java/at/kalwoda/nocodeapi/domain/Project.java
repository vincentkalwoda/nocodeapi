package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

@jakarta.persistence.Entity
@Table(name = "projects")
public class Project {
    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "api_key", nullable = false, unique = true))
    ApiKey apiKey;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Project name must not be blank!")
    String name;

    @Column(name = "description", nullable = true)
    String description;

    @Column(name = "prompt_text", nullable = false)
    @NotBlank(message = "Prompt text must not be blank!")
    String promptText;

    @Column(name = "created_at", nullable = false)
    Date createdAt = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_api_key", referencedColumnName = "api_key", nullable = false)
    User user;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    List<EntityModel> entities;
}
