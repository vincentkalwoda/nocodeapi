package at.kalwoda.nocodeapi.persistance.converter;

import at.kalwoda.nocodeapi.domain.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, Character> {
    static final String VALID_VALUES = "'U', 'A'";
    public static final String COLUMN_DEFINITION = "enum (" + VALID_VALUES + ")";

    @Override
    public Character convertToDatabaseColumn(UserRole attribute) {
        return switch (attribute) {
            case USER -> 'U';
            case ADMIN -> 'A';
            case null -> null;
        };
    }

    @Override
    public UserRole convertToEntityAttribute(Character dbData) {
        return switch (dbData) {
            case 'U' -> UserRole.USER;
            case 'A' -> UserRole.ADMIN;
            case null -> null;
            default -> throw new IllegalArgumentException("Unknown user role: " + dbData);
        };

    }
}
