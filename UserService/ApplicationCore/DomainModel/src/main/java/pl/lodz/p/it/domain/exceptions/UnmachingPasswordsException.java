package pl.lodz.p.it.domain.exceptions;

public class UnmachingPasswordsException extends Exception {
    public UnmachingPasswordsException() {
        super("Given password is wrong!");
    }
}
