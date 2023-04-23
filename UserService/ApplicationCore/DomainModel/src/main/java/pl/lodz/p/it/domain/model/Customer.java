package pl.lodz.p.it.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends User {

    private String email;
    private boolean isActive;

    public Customer(String username, String password, String email) {
        super(username, password, CustomerType.CUSTOMER);
        this.email = email;
        this.isActive = false;
    }

    public Customer(UUID id, String username, String password, String email, boolean isActive) {
        super(id, username, password, CustomerType.CUSTOMER);
        this.email = email;
        this.isActive = isActive;
    }

    public Customer(String username, String password, String email, boolean isActive) {
        super(username, password, CustomerType.CUSTOMER);
        this.email = email;
        this.isActive = isActive;
    }
}
