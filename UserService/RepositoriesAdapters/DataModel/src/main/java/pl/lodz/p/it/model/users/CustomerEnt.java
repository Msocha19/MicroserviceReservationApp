package pl.lodz.p.it.model.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@DiscriminatorValue("Customer")
@NoArgsConstructor
public class CustomerEnt extends UserEnt {

    @Column(unique = true)
    private String email;

    @Column
    private boolean isActive;

    public CustomerEnt(String username, String password, CustomerTypeEnt type, String email) {
        super(username, password, type);
        this.email = email;
        this.isActive = false;
    }

    public CustomerEnt(UUID userID, String username, String password, CustomerTypeEnt type, String email) {
        super(userID, username, password, type);
        this.email = email;
        this.isActive = false;
    }
}
