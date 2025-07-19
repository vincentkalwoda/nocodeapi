package at.kalwoda.nocodeapi.persistance.converter;

import at.kalwoda.nocodeapi.domain.Email;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailConverterTest {

    private EmailConverter emailConverter = new EmailConverter();

    @Test
    void valid_Email_ShouldConvertToString() {
        String emailValue = "vincent.kalwoda07@gmail.com";
        Email email = new Email(emailValue);

        var convertedValue = emailConverter.convertToDatabaseColumn(email);

        assertThat(convertedValue).isEqualTo(emailValue);
    }

    @Test
    void null_Email_ShouldConvertToNull() {
        assertThat(emailConverter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void valid_String_ShouldConvertToEmail() {
        String emailValue = "vincent.kalwoda07@gmail.com";

        var convertedValue = emailConverter.convertToEntityAttribute(emailValue);

        assertThat(convertedValue.value()).isEqualTo(emailValue);
    }

    @Test
    void null_String_ShouldConvertToNullEmail() {
        assertThat(emailConverter.convertToEntityAttribute(null)).isNull();
    }
}
