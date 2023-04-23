package pl.lodz.p.it.ports.view;

import com.nimbusds.jose.JOSEException;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.Client;
import pl.lodz.p.it.domain.model.Reservation;
import java.util.List;
import java.util.UUID;

@Component
public interface ClientServicePort {

    Client createClient(Client client);

    Client getClientById(UUID id);

    List<Reservation> getReservations(UUID id);

    List<Reservation> getFutureReservations(UUID id);

    List<Reservation> getPastReservations(UUID id);

//    void deleteUser(UUID id);

    Client getUserFromServerContext();

    void activateClient(UUID id);

    void deactivateClient(UUID id);
}
