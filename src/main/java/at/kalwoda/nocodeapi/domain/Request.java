package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

@Entity
@Table(name = "requests")
public class Request {
    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "api_key", nullable = false, unique = true))
    private ApiKey apiKey;

    @Column(name = "path", nullable = false)
    @NotBlank(message = "Request path must not be blank!")
    private String path;

    @Column(name = "method", nullable = false)
    @NotNull(message = "Request method must not be blank!")
    @Enumerated(EnumType.STRING)
    private MethodTypes method;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "query_params", columnDefinition = "TEXT")
    private String queryParams;

    @Column(name = "headers", columnDefinition = "TEXT")
    private String headers;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

    @Column(name = "status_code", nullable = false)
    private int statusCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "response_time", nullable = false)
    private Long responseTime;

    @Column(name = "user_agent", nullable = false)
    private String userAgent;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_api_key", nullable = false)
    Project project;

    @ElementCollection
    @CollectionTable(name = "request_queryparams", joinColumns = @JoinColumn(name = "request_api_key"))
    private List<QueryParams> queryParamsList = new ArrayList<>();
}
