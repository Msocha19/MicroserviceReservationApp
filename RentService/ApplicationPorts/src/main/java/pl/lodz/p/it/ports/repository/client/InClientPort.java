package pl.lodz.p.it.ports.repository.client;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.exceptions.DatabaseException;
import pl.lodz.p.it.domain.model.Client;
import java.util.UUID;

@Component
public interface InClientPort {

    Client createClient(Client user) throws DatabaseException;

    void removeReservation(UUID id, UUID reservationID);

    void addReservation(UUID id, UUID reservationID);

    void changeClientActivityClient(UUID id, boolean ifActivate) throws DatabaseException;
}
