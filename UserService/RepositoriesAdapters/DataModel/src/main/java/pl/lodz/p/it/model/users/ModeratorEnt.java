package pl.lodz.p.it.model.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@DiscriminatorValue("Moderator")
@NoArgsConstructor
public class ModeratorEnt extends UserEnt {

    @Column(unique = true)
    private String email;

    public ModeratorEnt(String username, String password, CustomerTypeEnt type, String email) {
        super(username, password, type);
        this.email = email;
    }

    public ModeratorEnt(UUID userID, String username, String password, CustomerTypeEnt type, String email) {
        super(userID, username, password, type);
        this.email = email;
    }
}
