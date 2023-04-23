package pl.lodz.p.it.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.domain.model.Client;
import pl.lodz.p.it.domain.model.Reservation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ClientDTO {
	public UUID clientID;
	public List<Reservation> reservations;

	public boolean active;

	public ClientDTO(Client c) {
		clientID = c.getId();
		active = c.isActive();
		reservations = new ArrayList<>();
		reservations.addAll(c.getReservations());
	}
}
