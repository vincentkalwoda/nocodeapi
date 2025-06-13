package at.kalwoda.nocodeapi.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void valid_Email_ShouldCreateInstance() {
        Email email = new Email("vincent.kalwoda07@gmail.com");
        assertNotNull(email);
        assertEquals("vincent.kalwoda07@gmail.com", email.value()); // Zugriff auf value()
    }


    @Test
    void null_Email_ShouldThrowException() {
        Exception exception = assertThrows(Email.EmailException.class, () -> new Email(null));
        assertEquals("Email must not be null!", exception.getMessage());
    }

    @Test
    void empty_Email_ShouldThrowException() {
        Exception exception = assertThrows(Email.EmailException.class, () -> new Email(""));
        assertEquals("Email is invalid!", exception.getMessage());
    }

    @Test
    void invalid_Email_ShouldThrowException() {
        Exception exception = assertThrows(Email.EmailException.class, () -> new Email("invalid-email"));
        assertEquals("Email is invalid!", exception.getMessage());
    }
}