package pl.lodz.p.it.services;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.ports.view.UserServicePort;
import pl.lodz.p.it.domain.model.*;
import pl.lodz.p.it.ports.repository.user.InUserPort;
import pl.lodz.p.it.ports.repository.user.OutUserPort;
import pl.lodz.p.it.domain.exceptions.UnmachingPasswordsException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService  implements UserDetailsService, UserServicePort {

    @Autowired
    private ApplicationAvailability applicationAvailability;

    @Autowired
    private InUserPort inUserPort;

    @Autowired
    private OutUserPort outUserPort;

    private final PasswordEncoder encoder;

    private final RabbitTemplate rabbitTemplate;

    public void checkServerReadiness() {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpUriRequest request = RequestBuilder.get()
                .setUri("http://localhost:8081/actuator/health")
                .build();
            httpclient.execute(request);
            if (applicationAvailability.getReadinessState() == ReadinessState.REFUSING_TRAFFIC) {
                System.out.println("RENT SERVICE IS DOWN!");
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot add user because RentService is not ready!");
            } else {
                System.out.println("RENT SERVICE IS RUNNING!");
            }
        } catch (IOException e) {
        }
    }
    @Override
    public List<Customer> getAllCustomers() {
        return outUserPort.findCustomers();
    }

    @Override
    public List<Customer> getCustomersByNameContaining(String name) {
        return outUserPort.findCustomerByUsernamePart(name);
    }

    @Override
    public List<Customer> getCustomerByExactName(String name) {
        return outUserPort.getCustomerByName(name);
    }

    @Override
    public List<Moderator> getModeratorByNameContaining(String name) {
        return outUserPort.findModeratorByUsernamePart(name);
    }

    @Override
    public List<Moderator> getModeratorByExactName(String name) {
        return outUserPort.getModeratorByName(name);
    }

    @Override
    public List<Administrator> getAdministratorByNameContaining(String name) {
        return outUserPort.findAdministratorByUsernamePart(name);
    }

    @Override
    public List<Administrator> getAdministratorByExactName(String name) {
        return outUserPort.getAdministratorByName(name);
    }

    @Override
    public List<Moderator> getAllModerators() {
        return outUserPort.findModerators();
    }

    @Override
    public List<Administrator> getAllAdministrators() {
        return outUserPort.findAdministrators();
    }

    @Override
    public Customer getCustomer(UUID id) {
        User user = outUserPort.getUser(id);
        if (user.getType() != CustomerType.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return (Customer) user;
    }

    @Override
    public Moderator getModerator(UUID id) {
        User user = outUserPort.getUser(id);
        if (user.getType() != CustomerType.MODERATOR) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return (Moderator) user;
    }

    @Override
    public Administrator getAdministrator(UUID id) {
        User user = outUserPort.getUser(id);
        if (user.getType() != CustomerType.ADMINISTRATOR) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return (Administrator) user;
    }

    @Override
    @Timed(value = "user.update.time", description = "Time taken to update a user")
    public User updateUser(User user) {
        if (!Objects.equals(user.getPassword(), outUserPort.getUser(user.getUserID()).getPassword())) {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        return inUserPort.modifyUser(user);
    }

    @Override
    @Timed(value = "user.change.isActive.time", description = "Time taken to change user's activity")
    public User changeActiveCustomer(UUID id, boolean ifActivate, boolean fromResponse) {
        checkServerReadiness();
        Customer customer = this.getCustomer(id);
        if (customer.isActive() == ifActivate) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
        customer.setActive(ifActivate);
        if (!fromResponse) {
            JSONObject jsonObject = new JSONObject();
            UUID messageId = UUID.randomUUID();
            jsonObject.put("messageId", messageId);
            jsonObject.put("time", LocalDateTime.now());
            jsonObject.put("customerId", id);
            if (ifActivate) {
                jsonObject.put("method", "activateCustomer");
            } else {
                jsonObject.put("method", "deactivateCustomer");
            }
            rabbitTemplate.convertAndSend("tks-exchange", "event.changeActiveCustomer", jsonObject.toString());
        }
        return this.updateUser(customer);
    }

    @Override
    public User getUserFromServerContext() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public User getUserByUsername(String username) {
        return outUserPort.findUserByUsername(username);
    }

    @Override
    @Timed(value = "administrator.creation.time", description = "Time taken to create a administrator")
    public Administrator createAdministrator(Administrator administrator) throws DataIntegrityViolationException {
        administrator.setType(CustomerType.ADMINISTRATOR);
        administrator.setPassword(encoder.encode(administrator.getPassword()));
        return (Administrator) inUserPort.createUser(administrator);
    }

    @Override
    @Timed(value = "moderator.creation.time", description = "Time taken to create a moderator")
    public Moderator createModerator(Moderator moderator) throws DataIntegrityViolationException {
        moderator.setType(CustomerType.MODERATOR);
        moderator.setPassword(encoder.encode(moderator.getPassword()));
        return (Moderator) inUserPort.createUser(moderator);
    }

    @Override
    @Timed(value = "customer.creation.time", description = "Time taken to create a customer")
    public Customer createCustomer(Customer customer)  throws DataIntegrityViolationException {
        checkServerReadiness();
        customer.setPassword(encoder.encode(customer.getPassword()));
        inUserPort.createUser(customer);
        Customer foundCustomer = (Customer) outUserPort.findUserByUsername(customer.getUsername());
        customer.setType(CustomerType.CUSTOMER);
        customer.setPassword(encoder.encode(customer.getPassword()));
        JSONObject jsonObject = new JSONObject();
        UUID messageId = UUID.randomUUID();
        jsonObject.put("messageId", messageId);
        jsonObject.put("method", "createCustomer");
        jsonObject.put("time", LocalDateTime.now());
        jsonObject.put("customerId", foundCustomer.getUserID());
        rabbitTemplate.convertAndSend("tks-exchange", "event.createCustomer", jsonObject.toString());
        return foundCustomer;
    }

    @Override
    @Timed(value = "user.delete.time", description = "Time taken to delete a user")
    public void deleteUser(UUID id) {
        inUserPort.deleteUser(id);
    }

    @Override
    public User loadUserByUsername(String username) {
        return outUserPort.findUserByUsername(username);
    }

    @Override
    @Timed(value = "user.passwordChange.time", description = "Time taken to change user password")
    public void changePassword(String oldPassword, String newPassword) throws UnmachingPasswordsException {
        User user = this.getUserFromServerContext();
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new UnmachingPasswordsException();
        }
        user.setPassword(newPassword);
        updateUser(user);
    }
}