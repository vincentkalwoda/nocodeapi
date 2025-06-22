package at.kalwoda.nocodeapi.persistance.converter;

import at.kalwoda.nocodeapi.domain.RelationshipType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RelationshipTypeConverterTest {
    private final RelationshipTypeConverter relationshipTypeConverter = new RelationshipTypeConverter();

    @Test
    void valid_relationshipType_ShouldConvertToDatabaseColumn() {
        RelationshipType oneToOne = RelationshipType.ONE_TO_ONE;
        RelationshipType oneToMany = RelationshipType.ONE_TO_MANY;
        RelationshipType manyToOne = RelationshipType.MANY_TO_ONE;
        RelationshipType manyToMany = RelationshipType.MANY_TO_MANY;

        String resultOneToOne = relationshipTypeConverter.convertToDatabaseColumn(oneToOne);
        String resultOneToMany = relationshipTypeConverter.convertToDatabaseColumn(oneToMany);
        String resultManyToOne = relationshipTypeConverter.convertToDatabaseColumn(manyToOne);
        String resultManyToMany = relationshipTypeConverter.convertToDatabaseColumn(manyToMany);
        String resultNull = relationshipTypeConverter.convertToDatabaseColumn(null);

        assertEquals("1:1", resultOneToOne);
        assertEquals("1:n", resultOneToMany);
        assertEquals("n:1", resultManyToOne);
        assertEquals("n:m", resultManyToMany);
        assertNull(resultNull);
    }

    @Test
    void null_relationshipType_ShouldConvertToDatabaseColumn() {
        RelationshipType relationshipType = null;

        String result = relationshipTypeConverter.convertToDatabaseColumn(relationshipType);

        assertNull(result);
    }

    @Test
    void valid_dbData_ShouldConvertToEntityAttribute() {
        String oneToOne = "1:1";
        String oneToMany = "1:n";
        String manyToOne = "n:1";
        String manyToMany = "n:m";
        String invalidChar = "invalid";
        String nullChar = null;

        RelationshipType oneToOneResult = relationshipTypeConverter.convertToEntityAttribute(oneToOne);
        RelationshipType oneToManyResult = relationshipTypeConverter.convertToEntityAttribute(oneToMany);
        RelationshipType manyToOneResult = relationshipTypeConverter.convertToEntityAttribute(manyToOne);
        RelationshipType manyToManyResult = relationshipTypeConverter.convertToEntityAttribute(manyToMany);
        RelationshipType nullResult = relationshipTypeConverter.convertToEntityAttribute(nullChar);

        assertEquals(RelationshipType.ONE_TO_ONE, oneToOneResult);
        assertEquals(RelationshipType.ONE_TO_MANY, oneToManyResult);
        assertEquals(RelationshipType.MANY_TO_ONE, manyToOneResult);
        assertEquals(RelationshipType.MANY_TO_MANY, manyToManyResult);
        assertThrows(IllegalArgumentException.class, () -> relationshipTypeConverter.convertToEntityAttribute(invalidChar));
        assertNull(nullResult);

    }

    @Test
    void null_dbData_ShouldConvertToEntityAttribute() {
        String dbData = null;

        RelationshipType result = relationshipTypeConverter.convertToEntityAttribute(dbData);

        assertNull(result);
    }
}