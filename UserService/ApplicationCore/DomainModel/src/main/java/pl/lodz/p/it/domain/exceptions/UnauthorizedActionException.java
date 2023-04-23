package pl.lodz.p.it.domain.exceptions;


import pl.lodz.p.it.domain.model.CustomerType;

public class UnauthorizedActionException extends Exception{

    public UnauthorizedActionException(CustomerType type) {
        super("Niedozwolona operacja dla klienta typu " + type.toString() + "!");
    }
}
