package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Field {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "api_key", nullable = false, unique = true))
    ApiKey apiKey;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FieldType type;

    @Column(nullable = false)
    private Boolean isRequired = false;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "relation_target_api_key"))
    private ApiKey relationTarget;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "relation_type")
    private RelationshipType relationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_api_key", nullable = false)
    private EntityModel entity;
}
