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
public class Client {

    private UUID id;

    private List<Reservation> reservations = new ArrayList<>();

    private boolean active = false;

    public Client(UUID id) {
        this.id = id;
    }
}
