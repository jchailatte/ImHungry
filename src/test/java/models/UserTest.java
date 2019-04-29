package models;

import common.SQLHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private static final String EXISTING_USERNAME = "existingUsername";
    private static final String USER_PASSWORD = "letmein123";
    private static User existingUser;

    @BeforeAll
    static void setUp() {
        SQLHelper.executeSetUpScript();
        existingUser = User.register(
                EXISTING_USERNAME, USER_PASSWORD
        );
    }

    @Test
    void loginExistingUserTest() {
        User loggedInUser = User.login(EXISTING_USERNAME, USER_PASSWORD);
        assertNotNull(loggedInUser);
        assertEquals(existingUser.getUserID(), loggedInUser.getUserID());
    }

    @Test
    void loginExistingUserWrongPasswordTest() {
        User invalidUser = User.login(EXISTING_USERNAME, "wrongpassword123");
        assertNull(invalidUser);
    }

    @Test
    void loginInvalidUsernameTest() {
        User invalidUser = User.login("invalidusername", USER_PASSWORD);
        assertNull(invalidUser);
    }

    @Test
    void loginBlankFields() {
        User invalidUser = User.login("", "");
        assertNull(invalidUser);
    }

    @Test
    void registerNewUserTest() {
        User newUser = User.register("newusername", USER_PASSWORD);
        assertNotNull(newUser);
        assertNotEquals(existingUser.getUserID(), newUser.getUserID());
    }

    @Test
    void registerExistingUserTest() {
        User invalidUser = User.register(EXISTING_USERNAME, USER_PASSWORD);
        assertNull(invalidUser);
    }

    @Test
    void registerWeakPasswordTest() {
        User invalidUser = User.register("newusername2", "hi");
        assertNull(invalidUser);
    }

    @Test
    void registerBlankFields() {
        User invalidUser = User.register("", "");
        assertNull(invalidUser);
    }

    @Test
    void fromJwtValidTest() {
        String validJwt = existingUser.generateJwt(User.STANDARD_EXPIRY_TIME);
        User validUser = User.fromJwt(validJwt);
        assertNotNull(validUser);
        assertEquals(existingUser.getUserID(), validUser.getUserID());
    }

    @Test
    void fromJwtInvalidTest() {
        String invalidJwt = "invalid.jwt.token";
        User invalidUser = User.fromJwt(invalidJwt);
        assertNull(invalidUser);
    }

    @Test
    void fromJwtExpiredTest() throws InterruptedException {
        String expiredJwt = existingUser.generateJwt(0);
        Thread.sleep(100);
        User invalidUser = User.fromJwt(expiredJwt);
        assertNull(invalidUser);
    }

    @Test
    void canSaltAndHashPassword() {
        String rawPassword = "rawpassword123";

        byte[] encryptedPassword1 = User.saltAndHashPassword(
                User.generateSalt(), rawPassword
        );
        byte[] encryptedPassword2 = User.saltAndHashPassword(
                User.generateSalt(), rawPassword
        );

        assertNotNull(encryptedPassword1);
        assertNotNull(encryptedPassword2);

        // Ensure passwords are different
        assertFalse(Arrays.equals(encryptedPassword1, encryptedPassword2));
    }

    @Test
    void passwordIsSecure() {
        String tooShortNoNumbers = "hi";
        String tooShortWithNumbers = "hi1";
        String noNumbers = "thisisapassword";

        assertFalse(User.passwordIsSecure(tooShortNoNumbers));
        assertFalse(User.passwordIsSecure(tooShortWithNumbers));
        assertFalse(User.passwordIsSecure(noNumbers));
        assertTrue(User.passwordIsSecure(USER_PASSWORD));
    }

    @Test
    void SQLInjection() {
        String jwt = existingUser.generateJwt(User.STANDARD_EXPIRY_TIME);

        String dropTable = "DROP TABLE users;";
        String selectUsers = "SELECT * FROM users WHERE 1";
        User newUser = User.register(dropTable, selectUsers);
        assertNotNull(newUser);

        // Ensure users were not deleted and existing
        // user can still be selected from DB
        assertNotNull(User.fromJwt(jwt));
    }
}
