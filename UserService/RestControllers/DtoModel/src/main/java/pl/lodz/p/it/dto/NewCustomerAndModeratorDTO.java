package pl.lodz.p.it.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCustomerAndModeratorDTO {
    public String username;
    public String password;
    public String email;
}
