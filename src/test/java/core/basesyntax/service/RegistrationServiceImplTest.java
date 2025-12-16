package core.basesyntax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import core.basesyntax.db.Storage;
import core.basesyntax.model.User;
import core.basesyntax.service.exceptions.InvalidRegistrationDataException;
import core.basesyntax.service.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistrationServiceImplTest {
    private static RegistrationServiceImpl registrationService;
    private static final int MIN_LOGIN_PASS_LENGTH = 6;
    private static final int LONG_LOGIN_PASS_LENGTH = MIN_LOGIN_PASS_LENGTH * 3;
    private static final int MIN_AGE = 18;
    private static final String MIN_VALID_LOGIN = "JohnDo";
    private static final String MIN_VALID_PASS = "123456";

    @BeforeAll
    static void beforeAll() {
        registrationService = new RegistrationServiceImpl();
    }

    @BeforeEach
    void setUp() {
        Storage.people.clear();
    }

    @AfterEach
    void tearDown() {
        Storage.people.clear();
    }

    @Test
    void register_nullInput_notOk() {
        InvalidRegistrationDataException expected = assertThrows(
                InvalidRegistrationDataException.class,
                () -> registrationService.register(null)
        );
        assertTrue(expected.getMessage().contains("User object cannot be null."));
    }

    @Test
    void register_nullLogin_notOk() {
        User user = new User();
        user.setLogin(null);
        InvalidRegistrationDataException expected = assertThrows(
                InvalidRegistrationDataException.class,
                () -> registrationService.register(user)
        );
        assertTrue(expected.getMessage().contains("Login cannot be null or empty."));
    }

    @Test
    void register_nullPassword_notOk() {
        User user = new User();
        user.setLogin(MIN_VALID_LOGIN);
        user.setPassword(null);
        InvalidRegistrationDataException expected = assertThrows(
                InvalidRegistrationDataException.class,
                () -> registrationService.register(user)
        );
        assertTrue(expected.getMessage().contains("Password cannot be null or empty."));
    }

    @Test
    void register_nullAge_notOk() {
        User user = new User();
        user.setLogin(MIN_VALID_LOGIN);
        user.setPassword(MIN_VALID_LOGIN);
        user.setAge(null);
        InvalidRegistrationDataException expected = assertThrows(
                InvalidRegistrationDataException.class,
                () -> registrationService.register(user)
        );
        assertTrue(expected.getMessage().contains("Age cannot be null."));
    }

    @Test
    void register_shortOrEmptyLogin_notOk() {
        String login = "";
        User user = new User();
        user.setLogin(login);
        InvalidRegistrationDataException expected = assertThrows(
                InvalidRegistrationDataException.class,
                () -> registrationService.register(user)
        );
        assertTrue(expected.getMessage().contains("Login cannot be null or empty."));

        for (int i = 0; i < MIN_LOGIN_PASS_LENGTH - 1; i++) {
            login += i;
            user.setLogin(login);
            InvalidRegistrationDataException expectedIteration = assertThrows(
                    InvalidRegistrationDataException.class,
                    () -> registrationService.register(user)
            );
            assertTrue(expectedIteration.getMessage().contains("Login must have at least"));
        }
    }

    @Test
    void register_shortOrEmptyPassword_notOk() {
        String login = MIN_VALID_LOGIN;
        String password = "";

        User user = new User();
        user.setLogin(login);
        user.setPassword(password);

        InvalidRegistrationDataException expected = assertThrows(
                InvalidRegistrationDataException.class,
                () -> registrationService.register(user)
        );
        assertTrue(expected.getMessage().contains("Password cannot be null or empty."));

        for (int i = 0; i < MIN_LOGIN_PASS_LENGTH - 1; i++) {
            login += i;
            password += i;
            user.setLogin(login);
            user.setPassword(password);
            InvalidRegistrationDataException expectedIteration = assertThrows(
                    InvalidRegistrationDataException.class,
                    () -> registrationService.register(user)
            );
            assertTrue(expectedIteration.getMessage().contains("Password must have at least"));
        }
    }

    @Test
    void register_negativeOrSmallAge_notOk() {
        User user = new User();
        user.setPassword(MIN_VALID_PASS);

        for (int i = -2; i < MIN_AGE; i++) {
            user.setLogin(MIN_VALID_LOGIN + i);
            user.setAge(i);
            InvalidRegistrationDataException expected = assertThrows(
                    InvalidRegistrationDataException.class,
                    () -> registrationService.register(user)
            );
            assertTrue(expected.getMessage().contains("Invalid age:"));
        }
    }

    @Test
    void register_sameLogin_notOk() {
        User user = new User();
        user.setLogin(MIN_VALID_LOGIN);
        user.setPassword(MIN_VALID_PASS);
        user.setAge(18);

        User result = registrationService.register(user);

        assertNotNull(result);
        assertEquals(user, result, "Returned user must be the same object that was passed.");
        assertNotNull(result.getId(), "Id should be set after adding to storage");
        assertTrue(Storage.people.contains(result), "User must be present in Storage.people");

        UserAlreadyExistsException expected = assertThrows(
                UserAlreadyExistsException.class,
                () -> registrationService.register(user)
        );
        assertTrue(expected.getMessage().contains("already exists"));
    }

    @Test
    void register_minimumValidData_ok() {
        String login = MIN_VALID_LOGIN;
        String password = MIN_VALID_PASS;
        int age = MIN_AGE;

        // Edge case: Minimum valid data (login/pass length 6, age 18)
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setAge(age);

        User result = registrationService.register(user);

        assertNotNull(result);
        assertEquals(user, result, "Returned user must be the same object that was passed.");
        assertNotNull(result.getId(), "Id should be set after adding to storage");
        assertTrue(Storage.people.contains(result), "User must be present in Storage.people");

        // Extended valid data (long login/pass, age > 18)
        for (int i = 0; i < LONG_LOGIN_PASS_LENGTH; i++) {
            User userIterator = new User();

            // Ensure unique login for each iteration
            userIterator.setLogin(login + i);
            userIterator.setPassword(password + i);
            userIterator.setAge(age + i);

            User resultIterator = registrationService.register(userIterator);

            assertNotNull(resultIterator);
            assertEquals(userIterator, resultIterator,
                    "Returned user must be the same object that was passed.");
            assertNotNull(resultIterator.getId(),
                    "Id should be set after adding to storage");
            assertTrue(Storage.people.contains(resultIterator),
                    "User must be present in Storage.people");
        }
    }

    @Test
    void register_extendedValidLoginPasswordAge_Ok() {
        String login = MIN_VALID_LOGIN;
        String password = MIN_VALID_PASS;
        int age = MIN_AGE;

        // Extended valid data (long login/pass, age > 18)
        for (int i = 0; i < LONG_LOGIN_PASS_LENGTH; i++) {
            login += i;
            password += i;
            age += i;

            User userIterator = new User();

            // Ensure unique login for each iteration
            userIterator.setLogin(login);
            userIterator.setPassword(password);
            userIterator.setAge(age);

            User resultIterator = registrationService.register(userIterator);

            assertNotNull(resultIterator);
            assertEquals(userIterator, resultIterator,
                    "Returned user must be the same object that was passed.");
            assertNotNull(resultIterator.getId(),
                    "Id should be set after adding to storage");
            assertTrue(Storage.people.contains(resultIterator),
                    "User must be present in Storage.people");
        }
    }
}
