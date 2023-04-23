package pl.lodz.p.it.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.domain.model.CustomerType;
import pl.lodz.p.it.domain.model.Moderator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ModeratorDTO {

    public UUID moderatorID;
    public String email;
    public String username;
    public CustomerType type;

    public ModeratorDTO(Moderator m) {
        email = m.getEmail();
        username = m.getUsername();
        moderatorID = m.getUserID();
        type = m.getType();
    }

}
