package at.kalwoda.nocodeapi.persistance.converter;

import at.kalwoda.nocodeapi.domain.FieldType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTypeConverterTest {

    private final FieldTypeConverter fieldTypeConverter = new FieldTypeConverter();

    @Test
    void valid_fieldType_ShouldConvertToDatabaseColumn() {
        FieldType fieldTypeString = FieldType.STRING;
        FieldType fieldTypeInteger = FieldType.INTEGER;
        FieldType fieldTypeFloat = FieldType.FLOAT;
        FieldType fieldTypeBoolean = FieldType.BOOLEAN;
        FieldType fieldTypeDate = FieldType.DATE;

        Character resultString = fieldTypeConverter.convertToDatabaseColumn(fieldTypeString);
        Character resultInteger = fieldTypeConverter.convertToDatabaseColumn(fieldTypeInteger);
        Character resultFloat = fieldTypeConverter.convertToDatabaseColumn(fieldTypeFloat);
        Character resultBoolean = fieldTypeConverter.convertToDatabaseColumn(fieldTypeBoolean);
        Character resultDate = fieldTypeConverter.convertToDatabaseColumn(fieldTypeDate);
        Character resultNull = fieldTypeConverter.convertToDatabaseColumn(null);

        assertEquals('S', resultString);
        assertEquals('I', resultInteger);
        assertEquals('F', resultFloat);
        assertEquals('B', resultBoolean);
        assertEquals('D', resultDate);
        assertNull(resultNull);
    }

    @Test
    void null_fieldType_ShouldConvertToDatabaseColumn() {
        FieldType fieldType = null;

        Character result = fieldTypeConverter.convertToDatabaseColumn(fieldType);

        assertNull(result);
    }

    @Test
    void valid_dbData_ShouldConvertToEntityAttribute() {
        Character string = 'S';
        Character integer = 'I';
        Character floatChar = 'F';
        Character booleanChar = 'B';
        Character date = 'D';
        Character invalidChar = 'X';
        Character nullChar = null;

        FieldType stringResult = fieldTypeConverter.convertToEntityAttribute(string);
        FieldType integerResult = fieldTypeConverter.convertToEntityAttribute(integer);
        FieldType floatResult = fieldTypeConverter.convertToEntityAttribute(floatChar);
        FieldType booleanResult = fieldTypeConverter.convertToEntityAttribute(booleanChar);
        FieldType dateResult = fieldTypeConverter.convertToEntityAttribute(date);
        FieldType nullResult = fieldTypeConverter.convertToEntityAttribute(nullChar);

        assertEquals(FieldType.STRING, stringResult);
        assertEquals(FieldType.INTEGER, integerResult);
        assertEquals(FieldType.FLOAT, floatResult);
        assertEquals(FieldType.BOOLEAN, booleanResult);
        assertEquals(FieldType.DATE, dateResult);
        assertThrows(IllegalArgumentException.class, () -> fieldTypeConverter.convertToEntityAttribute(invalidChar));
        assertNull(nullResult);

    }

    @Test
    void null_dbData_ShouldConvertToEntityAttribute() {
        Character dbData = null;

        FieldType result = fieldTypeConverter.convertToEntityAttribute(dbData);

        assertNull(result);
    }
}