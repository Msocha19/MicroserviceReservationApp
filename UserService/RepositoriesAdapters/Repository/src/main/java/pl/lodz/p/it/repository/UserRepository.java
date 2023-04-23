package pl.lodz.p.it.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lodz.p.it.model.users.AdministratorEnt;
import pl.lodz.p.it.model.users.CustomerEnt;
import pl.lodz.p.it.model.users.CustomerTypeEnt;
import pl.lodz.p.it.model.users.ModeratorEnt;
import pl.lodz.p.it.model.users.UserEnt;
import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEnt, UUID> {

    List<UserEnt> findUserEntByUsernameAndType(String username, CustomerTypeEnt customerTypeEnt);

    List<UserEnt> findUserEntByUsernameContainingAndType(String name, CustomerTypeEnt customerTypeEnt);

    List<UserEnt> findUserEntByType(CustomerTypeEnt type);

    UserEnt findUserEntByUsername(String username);
}
