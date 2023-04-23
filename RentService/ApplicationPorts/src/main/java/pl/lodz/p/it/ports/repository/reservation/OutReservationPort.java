package pl.lodz.p.it.ports.repository.reservation;

import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.Reservation;
import java.util.List;
import java.util.UUID;

@Component
public interface OutReservationPort {

    Reservation getReservation(UUID id);

    List<Reservation> getAllReservations();
}
