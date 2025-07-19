package at.kalwoda.nocodeapi.persistance.converter;

import at.kalwoda.nocodeapi.domain.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleConverterTest {

    private final UserRoleConverter userRoleConverter = new UserRoleConverter();

    @Test
    void valid_userRole_ShouldConvertToDatabaseColumn() {
        UserRole user = UserRole.USER;
        UserRole admin = UserRole.ADMIN;

        Character resultUser = userRoleConverter.convertToDatabaseColumn(user);
        Character resultAdmin = userRoleConverter.convertToDatabaseColumn(admin);
        Character resultNull = userRoleConverter.convertToDatabaseColumn(null);

        assertEquals('U', resultUser);
        assertEquals('A', resultAdmin);
        assertNull(resultNull);
    }

    @Test
    void null_userRole_ShouldConvertToDatabaseColumn() {
        UserRole userRole = null;

        Character result = userRoleConverter.convertToDatabaseColumn(userRole);

        assertNull(result);
    }

    @Test
    void valid_dbData_ShouldConvertToEntityAttribute() {
        Character user = 'U';
        Character admin = 'A';
        Character invalidChar = 'X';
        Character nullChar = null;

        UserRole userResult = userRoleConverter.convertToEntityAttribute(user);
        UserRole adminResult = userRoleConverter.convertToEntityAttribute(admin);
        UserRole nullResult = userRoleConverter.convertToEntityAttribute(nullChar);

        assertEquals(UserRole.USER, userResult);
        assertEquals(UserRole.ADMIN, adminResult);
        assertThrows(IllegalArgumentException.class, () -> userRoleConverter.convertToEntityAttribute(invalidChar));
        assertNull(nullResult);

    }

    @Test
    void null_dbData_ShouldConvertToEntityAttribute() {
        Character dbData = null;

        UserRole result = userRoleConverter.convertToEntityAttribute(dbData);

        assertNull(result);
    }

}