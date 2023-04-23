package pl.lodz.p.it.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lodz.p.it.model.ClientEnt;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEnt, UUID> {
}
