package core.basesyntax.service;

import core.basesyntax.dao.StorageDao;
import core.basesyntax.dao.StorageDaoImpl;
import core.basesyntax.model.User;
import core.basesyntax.service.exceptions.InvalidRegistrationDataException;
import core.basesyntax.service.exceptions.UserAlreadyExistsException;

public class RegistrationServiceImpl implements RegistrationService {
    private static final Integer MIN_AGE = 18;
    private static final Integer MIN_LOGIN_PASS_LENGTH = 6;

    private final StorageDao storageDao = new StorageDaoImpl();

    @Override
    public User register(User user) {
        validate(user);

        // Validation passed, proceed with storage
        return storageDao.add(user);
    }

    private void validate(User user) {
        // --- General Check ---
        if (user == null) {
            throw new InvalidRegistrationDataException("User object cannot be null.");
        }

        // --- Login Validation ---
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new InvalidRegistrationDataException("Login cannot be null or empty.");
        }
        if (user.getLogin().length() < MIN_LOGIN_PASS_LENGTH) {
            throw new InvalidRegistrationDataException("Login must have at least "
                    + MIN_LOGIN_PASS_LENGTH + " characters.");
        }
        if (storageDao.get(user.getLogin()) != null) {
            // Use a specific exception for existing user
            throw new UserAlreadyExistsException("Login '" + user.getLogin() + "' already exists.");
        }

        // --- Password Validation ---
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new InvalidRegistrationDataException("Password cannot be null or empty.");
        }
        if (user.getPassword().length() < MIN_LOGIN_PASS_LENGTH) {
            throw new InvalidRegistrationDataException("Password must have at least "
                    + MIN_LOGIN_PASS_LENGTH + " characters.");
        }

        // --- Age Validation ---
        if (user.getAge() == null) {
            throw new InvalidRegistrationDataException("Age cannot be null.");
        }
        if (user.getAge() < MIN_AGE) {
            throw new InvalidRegistrationDataException("Invalid age: " + user.getAge()
                    + ". Minimum allowed age is " + MIN_AGE + ".");
        }
    }
}
