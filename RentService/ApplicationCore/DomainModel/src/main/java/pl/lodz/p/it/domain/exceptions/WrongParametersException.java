package pl.lodz.p.it.domain.exceptions;

public class WrongParametersException extends RuntimeException {

    public WrongParametersException(String message) {
        super(message);
    }
}
