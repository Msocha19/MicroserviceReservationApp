package pl.lodz.p.it.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.domain.exceptions.WrongParametersException;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Administrator extends User {

    public Administrator(UUID userID, String username, String password) {
        super(userID, username, password, CustomerType.ADMINISTRATOR);
        if (username.isEmpty() || password.isEmpty()) {
            throw new WrongParametersException("User data cannot be empty!");
        }
    }

    public Administrator(String username, String password) {
        super(username, password, CustomerType.ADMINISTRATOR);
        if (username.isEmpty() || password.isEmpty()) {
            throw new WrongParametersException("User data cannot be empty!");
        }
    }
}
