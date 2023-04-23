package pl.lodz.p.it.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.domain.exceptions.WrongParametersException;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moderator extends User {

    private String email;

    public Moderator(String username, String password, String email) {
        super(username, password, CustomerType.MODERATOR);
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new WrongParametersException("User data cannot be empty!");
        }
        this.email = email;
    }

    public Moderator(UUID userID, String username, String password, String email) {
        super(userID, username, password, CustomerType.MODERATOR);
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new WrongParametersException("User data cannot be empty!");
        }
        this.email = email;
    }
}
