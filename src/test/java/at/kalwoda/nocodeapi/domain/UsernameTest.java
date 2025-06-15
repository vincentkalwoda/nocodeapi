package at.kalwoda.nocodeapi.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsernameTest {

    @Test
    void valid_username_ShouldCreateInstance() {
        String validUsername = "validUser123";
        Username username = new Username(validUsername);
        assertEquals(validUsername, username.value());
    }

    @Test
    void null_username_ShouldThrowException() {
        assertThrows(Username.UsernameException.class, () -> new Username(null));
    }

    @Test
    void empty_username_ShouldThrowException() {
        assertThrows(Username.UsernameException.class, () -> new Username(""));
    }

    @Test
    void username_with_special_characters_ShouldThrowException() {
        String invalidUsername = "invalid@User!";
        assertThrows(Username.UsernameException.class, () -> new Username(invalidUsername));
    }

    @Test
    void too_long_username_ShouldThrowException() {
        String longUsername = "a".repeat(256);
        assertThrows(Username.UsernameException.class, () -> new Username(longUsername));
    }

    @Test
    void too_short_username_ShouldThrowException() {
        String shortUsername = "ab";
        assertThrows(Username.UsernameException.class, () -> new Username(shortUsername));
    }
}