package at.kalwoda.nocodeapi.persistance.converter;

import at.kalwoda.nocodeapi.domain.FieldType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FieldTypeConverter implements AttributeConverter<FieldType, Character> {
    static final String VALID_VALUES = "'S', 'I', 'B', 'F', 'D'";
    public static final String COLUMN_DEFINITION = "enum (" + VALID_VALUES + ")";

    @Override
    public Character convertToDatabaseColumn(FieldType attribute) {
        return switch (attribute) {
            case STRING -> 'S';
            case INTEGER -> 'I';
            case BOOLEAN -> 'B';
            case FLOAT -> 'F';
            case DATE -> 'D';
            case null -> null;
        };
    }

    @Override
    public FieldType convertToEntityAttribute(Character dbData) {
        return switch (dbData) {
            case 'S' -> FieldType.STRING;
            case 'I' -> FieldType.INTEGER;
            case 'B' -> FieldType.BOOLEAN;
            case 'F' -> FieldType.FLOAT;
            case 'D' -> FieldType.DATE;
            case null -> null;
            default -> throw new IllegalArgumentException("Unknown field type: " + dbData);
        };
    }
}
