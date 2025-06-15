package at.kalwoda.nocodeapi.persistance.converter;

import at.kalwoda.nocodeapi.domain.ApiKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyConverterTest {

    private ApiKeyConverter converter = new ApiKeyConverter();

    @Test
    void valid_ApiKey_ShouldConvertToString() {
        String apiKeyString = "test-api";
        ApiKey apiKey = new ApiKey(apiKeyString);

        String convertedString = converter.convertToDatabaseColumn(apiKey);

        assertEquals(apiKeyString, convertedString);
    }

    @Test
    void null_ApiKey_ShouldConvertToNull() {
        ApiKeyConverter converter = new ApiKeyConverter();

        String convertedString = converter.convertToDatabaseColumn(null);

        assertNull(convertedString);
    }

    @Test
    void valid_String_ShouldConvertToApiKey() {
        String apiKeyString = "test-api";

        ApiKey apiKey = converter.convertToEntityAttribute(apiKeyString);

        assertEquals(apiKeyString, apiKey.value());
    }

    @Test
    void null_String_ShouldConvertToNullApiKey() {
        ApiKey apiKey = converter.convertToEntityAttribute(null);

        assertNull(apiKey);
    }
}