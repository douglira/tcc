package models;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void shouldBeValidHashedPassword() {
        User user = new User();
        user.setPassword("minhasenha1234");
        user.hashPassword();

        String password = "minhasenha1234";
        assertTrue(user.checkPassword(password));
    }

    @Test
    public void shouldBeInvalidHashedPassword() {
        User user = new User();
        user.setPassword("minhasenha1234");
        user.hashPassword();

        String password = "minhasenha1111";
        assertFalse(user.checkPassword(password));
    }
}