package pl.lodz.p.it.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.domain.model.Administrator;
import pl.lodz.p.it.domain.model.CustomerType;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AdministratorDTO {

    public UUID administratorID;
    public String username;
    public CustomerType type;

    public AdministratorDTO(Administrator a) {
        username = a.getUsername();
        administratorID = a.getUserID();
        type = a.getType();
    }

}
