package at.kalwoda.nocodeapi.persistance.converter;

import at.kalwoda.nocodeapi.domain.ApiKey;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;
import java.util.Optional;

@Converter(autoApply = true)
public class ApiKeyConverter implements AttributeConverter<ApiKey, String> {
    @Override
    public String convertToDatabaseColumn(ApiKey apiKey) {
        return Optional.ofNullable(apiKey)
                .map(ApiKey::value)
                .filter(Objects::nonNull)
                .orElse(null);
    }

    @Override
    public ApiKey convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return new ApiKey(s);
    }

}
