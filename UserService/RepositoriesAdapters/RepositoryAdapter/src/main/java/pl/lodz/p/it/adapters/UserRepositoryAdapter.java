package pl.lodz.p.it.adapters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.adapters.mapper.Mapper;
import pl.lodz.p.it.domain.model.Administrator;
import pl.lodz.p.it.domain.model.Customer;
import pl.lodz.p.it.domain.model.Moderator;
import pl.lodz.p.it.domain.model.User;
import pl.lodz.p.it.model.users.AdministratorEnt;
import pl.lodz.p.it.model.users.CustomerEnt;
import pl.lodz.p.it.model.users.CustomerTypeEnt;
import pl.lodz.p.it.model.users.ModeratorEnt;
import pl.lodz.p.it.model.users.UserEnt;
import pl.lodz.p.it.ports.repository.user.InUserPort;
import pl.lodz.p.it.ports.repository.user.OutUserPort;

import pl.lodz.p.it.repository.UserRepository;
import java.util.*;

@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserRepositoryAdapter implements InUserPort, OutUserPort {

    @Autowired
    UserRepository userEntRepository;

    @Autowired
    Mapper mapper;

    @Override
    public User createUser(User user) throws DataIntegrityViolationException {
        UserEnt userEnt = userEntRepository.save(mapper.mapDomainToEnt(user, false));
        return mapper.mapEntToDomain(userEnt, false);
    }

    @Override
    public User modifyUser(User user) {
        UserEnt userEnt = userEntRepository.findById(user.getUserID()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (userEnt.getType() == CustomerTypeEnt.CUSTOMER) {
            Customer customer = (Customer) user;
            CustomerEnt customerEnt = (CustomerEnt) userEnt;
            customerEnt.setActive(customer.isActive());
            customerEnt.setEmail(customer.getEmail());
            customerEnt.setPassword(customer.getPassword());
            customerEnt.setUsername(customer.getUsername());
            CustomerEnt modified = userEntRepository.save(customerEnt);
            return mapper.mapEntToDomain(modified, true);
        } else if (userEnt.getType() == CustomerTypeEnt.MODERATOR) {
            Moderator moderator = (Moderator) user;
            ModeratorEnt moderatorEnt = (ModeratorEnt) userEnt;
            moderatorEnt.setEmail(moderator.getEmail());
            moderatorEnt.setPassword(moderator.getPassword());
            moderatorEnt.setUsername(moderator.getUsername());
            ModeratorEnt modified = userEntRepository.save(moderatorEnt);
            return mapper.mapEntToDomain(modified, true);
        } else if (userEnt.getType() == CustomerTypeEnt.ADMINISTRATOR) {
            userEnt.setUsername(user.getUsername());
            userEnt.setPassword(user.getPassword());
            UserEnt modified = userEntRepository.save(userEnt);
            return mapper.mapEntToDomain(modified, true);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }



    @Override
    public void deleteUser(UUID id) {
        userEntRepository.deleteById(id);
    }

    @Override
    public User getUser(UUID id) {
        UserEnt userEnt = userEntRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.mapEntToDomain(userEnt, true);
    }

    @Override
    public List<User> getAllUsers() {
        return userEntRepository
                .findAll()
                .stream()
                .map(userEnt -> mapper.mapEntToDomain(userEnt, true))
                .toList();
    }

    public List<Customer> getCustomerByName(String name) {
        return userEntRepository.findUserEntByUsernameAndType(name, CustomerTypeEnt.CUSTOMER)
            .stream()
            .map(userEnt -> (Customer) mapper.mapEntToDomain(userEnt, true))
            .toList();
    }

    @Override
    public List<Customer> findCustomerByUsernamePart(String name) {
        return userEntRepository.findUserEntByUsernameContainingAndType(name, CustomerTypeEnt.CUSTOMER)
            .stream()
            .map(userEnt -> (Customer) mapper.mapEntToDomain(userEnt, true))
            .toList();
    }

    @Override
    public List<Moderator> getModeratorByName(String name) {
        return userEntRepository.findUserEntByUsernameAndType(name, CustomerTypeEnt.MODERATOR)
            .stream()
            .map(userEnt -> (Moderator) mapper.mapEntToDomain(userEnt, true))
            .toList();
    }

    @Override
    public List<Moderator> findModeratorByUsernamePart(String name) {
        return userEntRepository.findUserEntByUsernameContainingAndType(name, CustomerTypeEnt.MODERATOR)
            .stream()
            .map(userEnt -> (Moderator) mapper.mapEntToDomain(userEnt, true))
            .toList();
    }

    @Override
    public List<Administrator> getAdministratorByName(String name) {
        return userEntRepository.findUserEntByUsernameAndType(name, CustomerTypeEnt.ADMINISTRATOR)
            .stream()
            .map(userEnt -> (Administrator) mapper.mapEntToDomain(userEnt, true))
            .toList();
    }

    @Override
    public List<Administrator> findAdministratorByUsernamePart(String name) {
        return userEntRepository.findUserEntByUsernameContainingAndType(name, CustomerTypeEnt.ADMINISTRATOR)
            .stream()
            .map(userEnt -> (Administrator) mapper.mapEntToDomain(userEnt, true))
            .toList();
    }

    @Override
    public List<Customer> findCustomers() {
        return userEntRepository.findUserEntByType(CustomerTypeEnt.CUSTOMER)
            .stream()
            .map(userEnt -> (Customer) mapper.mapEntToDomain(userEnt, true)).toList();
    }

    @Override
    public List<Moderator> findModerators() {
        return userEntRepository.findUserEntByType(CustomerTypeEnt.MODERATOR)
            .stream()
            .map(userEnt -> (Moderator) mapper.mapEntToDomain(userEnt, true)).toList();
    }

    @Override
    public List<Administrator> findAdministrators() {
        return userEntRepository.findUserEntByType(CustomerTypeEnt.ADMINISTRATOR)
            .stream()
            .map(userEnt -> (Administrator) mapper.mapEntToDomain(userEnt, true)).toList();
    }

    @Override
    public User findUserByUsername(String username) {
        return mapper.mapEntToDomain(userEntRepository.findUserEntByUsername(username), true);
    }
}
