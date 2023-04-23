package pl.lodz.p.it.ports.repository.reservation;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.Reservation;
import java.util.UUID;

@Component
public interface InReservationPort {

    Reservation createReservation(Reservation reservation) throws DataIntegrityViolationException;

    void deleteReservation(UUID id);

    Reservation modifyReservation(Reservation reservation);
}
