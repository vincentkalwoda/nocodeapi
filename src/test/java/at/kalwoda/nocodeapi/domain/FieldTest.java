package at.kalwoda.nocodeapi.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    @Test
    void valid_Field_ShouldCreateInstance() {
        Field field = new Field("name", FieldType.STRING, false);
        assertNotNull(field);
        assertEquals("name", field.name());
        assertEquals(FieldType.STRING, field.type());
        assertFalse(field.isRequired());
    }

    @Test
    void null_required_ShouldDefaultToFalse() {
        Field field = new Field("name", FieldType.STRING, null);
        assertNotNull(field);
        assertEquals("name", field.name());
        assertEquals(FieldType.STRING, field.type());
        assertFalse(field.isRequired());
    }

    @Test
    void null_name_ShouldThrowException() {
        assertThrows(Field.FieldException.class, () -> new Field(null, FieldType.STRING, false));
    }

    @Test
    void empty_name_ShouldThrowException() {
        assertThrows(Field.FieldException.class, () -> new Field("", FieldType.STRING, false));
    }


    @Test
    void invalid_type_ShouldThrowException() {
        assertThrows(Field.FieldException.class, () -> new Field("name", null, false));
    }
}