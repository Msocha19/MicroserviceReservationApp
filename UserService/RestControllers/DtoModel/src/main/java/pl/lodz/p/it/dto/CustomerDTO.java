package pl.lodz.p.it.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.domain.model.Customer;
import pl.lodz.p.it.domain.model.CustomerType;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO {
    
	public UUID customerID;
	public String email;
	public String username;
	public boolean active;
	public CustomerType type;

	public CustomerDTO (Customer c) {
		email = c.getEmail();
		username = c.getUsername();
		customerID = c.getUserID();
		active = c.isActive();
		type = c.getType();
	}

}
