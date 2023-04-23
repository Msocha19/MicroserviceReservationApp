package pl.lodz.p.it.ports.view;

import com.nimbusds.jose.JOSEException;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.Reservation;
import java.util.List;
import java.util.UUID;

@Component
public interface ReservationServicePort {

    Reservation create (Reservation r) throws Exception;

    void delete (UUID id, boolean force);

    Reservation modify (Reservation reservation)  throws Exception ;

    Reservation get (UUID id);

    List<Reservation> getAll();

    List<Reservation> getCustomerReservations(UUID  id);

    List<Reservation> getProductReservations(UUID id);
}
