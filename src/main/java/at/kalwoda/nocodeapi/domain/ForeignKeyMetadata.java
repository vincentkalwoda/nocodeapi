package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record ForeignKeyMetadata(
        String targetEntity,
        String targetField,
        RelationshipType relationType
) {}

