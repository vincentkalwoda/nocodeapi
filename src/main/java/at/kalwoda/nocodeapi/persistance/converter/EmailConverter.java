package at.kalwoda.nocodeapi.persistance.converter;

import at.kalwoda.nocodeapi.domain.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;
import java.util.Optional;

@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {
    @Override
    public String convertToDatabaseColumn(Email email) {
        return Optional.ofNullable(email)
                .map(Email::value)
                .filter(Objects::nonNull)
                .orElse(null);
    }

    @Override
    public Email convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return new Email(s);
    }
}