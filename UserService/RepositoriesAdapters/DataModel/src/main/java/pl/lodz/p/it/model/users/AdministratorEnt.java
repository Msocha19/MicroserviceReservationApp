package pl.lodz.p.it.model.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@DiscriminatorValue("Administrator")
@NoArgsConstructor
public class AdministratorEnt extends UserEnt {

    public AdministratorEnt(UUID userID, String username, String password, CustomerTypeEnt type) {
        super(userID, username, password, type);
    }

    public AdministratorEnt(String username, String password, CustomerTypeEnt type) {
        super(username, password, type);
    }
}
