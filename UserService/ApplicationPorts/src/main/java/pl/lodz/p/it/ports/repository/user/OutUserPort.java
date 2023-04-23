package pl.lodz.p.it.ports.repository.user;

import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.Administrator;
import pl.lodz.p.it.domain.model.Customer;
import pl.lodz.p.it.domain.model.Moderator;
import pl.lodz.p.it.domain.model.User;

import java.util.List;
import java.util.UUID;

@Component
public interface OutUserPort {

    User getUser(UUID id);

    List<User> getAllUsers();

    List<Customer> getCustomerByName(String name);

    List<Customer> findCustomerByUsernamePart(String name);

    List<Moderator> getModeratorByName(String name);

    List<Moderator> findModeratorByUsernamePart(String name);

    List<Administrator> getAdministratorByName(String name);

    List<Administrator> findAdministratorByUsernamePart(String name);

    List<Customer> findCustomers();

    List<Moderator> findModerators();

    List<Administrator> findAdministrators();

    User findUserByUsername(String username);
}
