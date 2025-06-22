package at.kalwoda.nocodeapi.persistance.converter;

import at.kalwoda.nocodeapi.domain.RelationshipType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RelationshipTypeConverter implements AttributeConverter<RelationshipType, String> {
    static final String VALID_VALUES = "'1:1', '1:n', 'n:1', 'n:m'";
    public static final String COLUMN_DEFINITION = "enum (" + VALID_VALUES + ")";

    @Override
    public String convertToDatabaseColumn(RelationshipType attribute) {
        return switch (attribute) {
            case ONE_TO_ONE -> "1:1";
            case ONE_TO_MANY -> "1:n";
            case MANY_TO_ONE -> "n:1";
            case MANY_TO_MANY -> "n:m";
            case null -> null;
        };
    }

    @Override
    public RelationshipType convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "1:1" -> RelationshipType.ONE_TO_ONE;
            case "1:n" -> RelationshipType.ONE_TO_MANY;
            case "n:1" -> RelationshipType.MANY_TO_ONE;
            case "n:m" -> RelationshipType.MANY_TO_MANY;
            case null -> null;
            default -> throw new IllegalArgumentException("Unknown relationship type: " + dbData);
        };
    }

}

