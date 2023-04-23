package pl.lodz.p.it.ports.view;

import com.nimbusds.jose.JOSEException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.exceptions.UnmachingPasswordsException;
import pl.lodz.p.it.domain.model.Administrator;
import pl.lodz.p.it.domain.model.Customer;
import pl.lodz.p.it.domain.model.Moderator;
import pl.lodz.p.it.domain.model.User;
import java.util.List;
import java.util.UUID;

@Component
public interface UserServicePort {

    List<Customer> getAllCustomers();

    List<Customer> getCustomersByNameContaining(String name);

    List<Customer> getCustomerByExactName(String name);

    List<Moderator> getModeratorByNameContaining(String name);

    List<Moderator> getModeratorByExactName(String name);

    List<Administrator> getAdministratorByNameContaining(String name);

    List<Administrator> getAdministratorByExactName(String name);

    List<Moderator> getAllModerators();

    List<Administrator> getAllAdministrators();

    Customer getCustomer(UUID id);

    Moderator getModerator(UUID id);

    Administrator getAdministrator(UUID id);
    User updateUser(User user);

    User changeActiveCustomer(UUID id, boolean ifActivate, boolean fromResponse);

    User getUserFromServerContext();

    User getUserByUsername(String username);


    Administrator createAdministrator(Administrator administrator) throws DataIntegrityViolationException;

    Moderator createModerator(Moderator moderator) throws DataIntegrityViolationException;

    Customer createCustomer(Customer customer) throws DataIntegrityViolationException;

    void deleteUser(UUID id);

    void changePassword(String oldPassword, String newPassword) throws UnmachingPasswordsException;
}
