package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "endpoints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endpoint {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "api_key", nullable = false, unique = true))
    private ApiKey apiKey;

    @Column(nullable = false)
    @NotBlank(message = "Route must not be blank!")
    private String route;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Method type must not be null!")
    private MethodTypes method;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "SQL query must not be blank!")
    private String sql;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_api_key", nullable = false)
    private Project project;

    @ElementCollection
    @CollectionTable(name = "endpoint_parameters", joinColumns = @JoinColumn(name = "endpoint_id"))
    @Column(name = "parameter")
    private List<String> parameters;
}
