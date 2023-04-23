package pl.lodz.p.it.ports.repository.client;

import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.Client;
import java.util.List;
import java.util.UUID;

@Component
public interface OutClientPort {

    Client getUser(UUID id);

    List<Client> getAllUsers();
}
