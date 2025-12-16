package core.basesyntax.service;

import core.basesyntax.dao.StorageDao;
import core.basesyntax.dao.StorageDaoImpl;
import core.basesyntax.model.User;
import core.basesyntax.service.exceptions.RegistrationException;

public class RegistrationServiceImpl implements RegistrationService {
    private static final Integer MIN_AGE = 18;
    private static final Integer MIN_LOGIN_PASS_LENGTH = 6;

    private final StorageDao storageDao = new StorageDaoImpl();

    @Override
    public User register(User user) {
        validate(user);

        return storageDao.add(user);
    }

    private void validate(User user) {
        if (user == null) {
            throw new RegistrationException("User object cannot be null.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new RegistrationException("Login cannot be null or empty.");
        }
        if (user.getLogin().length() < MIN_LOGIN_PASS_LENGTH) {
            throw new RegistrationException("Login must have at least "
                    + MIN_LOGIN_PASS_LENGTH + " characters.");
        }
        if (storageDao.get(user.getLogin()) != null) {
            throw new RegistrationException("Login '" + user.getLogin() + "' already exists.");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RegistrationException("Password cannot be null or empty.");
        }
        if (user.getPassword().length() < MIN_LOGIN_PASS_LENGTH) {
            throw new RegistrationException("Password must have at least "
                    + MIN_LOGIN_PASS_LENGTH + " characters.");
        }

        if (user.getAge() == null) {
            throw new RegistrationException("Age cannot be null.");
        }
        if (user.getAge() < MIN_AGE) {
            throw new RegistrationException("Invalid age: " + user.getAge()
                    + ". Minimum allowed age is " + MIN_AGE + ".");
        }
    }
}
