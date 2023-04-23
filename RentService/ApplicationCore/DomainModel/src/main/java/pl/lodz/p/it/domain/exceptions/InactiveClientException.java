package pl.lodz.p.it.domain.exceptions;

public class InactiveClientException extends Exception {
    public InactiveClientException() {
        super("Client is inactive!");
    }
}
