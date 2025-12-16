package core.basesyntax.service.exceptions;

public class UserAlreadyExistsException extends RegistrationException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
