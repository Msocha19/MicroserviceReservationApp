package pl.lodz.p.it.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.domain.exceptions.UnmachingPasswordsException;
import pl.lodz.p.it.domain.exceptions.WrongParametersException;
import pl.lodz.p.it.domain.model.*;
import pl.lodz.p.it.dto.*;
import pl.lodz.p.it.ports.view.JwtProviderPort;
import pl.lodz.p.it.ports.view.UserServicePort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/")
public class UserEndpoint {

    @Autowired
    private UserServicePort userService;

    @Autowired
    private JwtProviderPort jwtProvider;

    private final AuthenticationManager authenticationManager;

    private List<CustomerDTO> changeCustomersToCustomerDTO(List<Customer> customers) {
        List<CustomerDTO> customersDTO = new ArrayList<>();
        for (Customer customer : customers) {
            customersDTO.add(new CustomerDTO(customer));
        }
        return customersDTO;
    }

    private List<ModeratorDTO> changeModeratorsToModeratorsDTO(List<Moderator> moderators) {
        List<ModeratorDTO> moderatorsDTO = new ArrayList<>();
        for (Moderator moderator : moderators) {
            moderatorsDTO.add(new ModeratorDTO(moderator));
        }
        return moderatorsDTO;
    }

    private List<AdministratorDTO> changeAdministratorsToAdministratorDTO(List<Administrator> administrators) {
        List<AdministratorDTO> administratorsDTO = new ArrayList<>();
        for (Administrator administrator : administrators) {
            administratorsDTO.add(new AdministratorDTO(administrator));
        }
        return administratorsDTO;
    }

    @GetMapping("/customer")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.get.all.customers", description = "Time taken to execute a get")
    @Counted(value = "user.endpoint.get.all.customers.error", recordFailuresOnly = true)
    public List<CustomerDTO> getAllCustomers(@Param("username") String username,
                                             @Param("exact") String exact) {
        List<CustomerDTO> data = null;
        if ((Objects.equals(username, "") || username == null) && (exact == null || Objects.equals(exact, ""))) {
            data = this.changeCustomersToCustomerDTO(userService.getAllCustomers());
        } else if (exact == null || Objects.equals(exact, "")) {
            data = this.changeCustomersToCustomerDTO(userService.getCustomersByNameContaining(username));
        } else if (Objects.equals(username, "") || username == null) {
            data = this.changeCustomersToCustomerDTO(userService.getCustomerByExactName(exact));
        }
        return data;
    }

    @GetMapping("/moderator")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.get.all.moderators", description = "Time taken to execute a get")
    @Counted(value = "user.endpoint.get.all.moderators.error", recordFailuresOnly = true)
    public List<ModeratorDTO> getAllModerators(@Param("username") String username,
                                               @Param("exact") String exact) {
        List<ModeratorDTO> data = null;
        if ((Objects.equals(username, "") || username == null) && (exact == null || Objects.equals(exact, ""))) {
            data = this.changeModeratorsToModeratorsDTO(userService.getAllModerators());
        } else if (exact == null || Objects.equals(exact, "")) {
            data = this.changeModeratorsToModeratorsDTO(userService.getModeratorByNameContaining(username));
        } else if (Objects.equals(username, "") || username == null) {
            data = this.changeModeratorsToModeratorsDTO(userService.getModeratorByExactName(exact));
        }
        return data;
    }

    @GetMapping("/administrator")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.get.all.administrators", description = "Time taken to execute a get")
    @Counted(value = "user.endpoint.get.administrators.error", recordFailuresOnly = true)
    public List<AdministratorDTO> getAllAdministrators(@Param("username") String username,
                                                       @Param("exact") String exact) {
        List<AdministratorDTO> data = null;
        if ((Objects.equals(username, "") || username == null) && (exact == null || Objects.equals(exact, ""))) {
            data = this.changeAdministratorsToAdministratorDTO(userService.getAllAdministrators());
        } else if (exact == null || Objects.equals(exact, "")) {
            data = this.changeAdministratorsToAdministratorDTO(userService.getAdministratorByNameContaining(username));
        } else if (Objects.equals(username, "") || username == null) {
            data = this.changeAdministratorsToAdministratorDTO(userService.getAdministratorByExactName(exact));
        }
        return data;
    }

    @GetMapping("/customer/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.get.customer", description = "Time taken to execute a get")
    @Counted(value = "user.endpoint.get.customer.error", recordFailuresOnly = true)
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable("id") UUID id) {
        try {
            Customer customer = userService.getCustomer(id);
            return ResponseEntity.ok().body(new CustomerDTO(customer));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/moderator/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.get.moderator", description = "Time taken to execute a get")
    @Counted(value = "user.endpoint.get.moderator.error", recordFailuresOnly = true)
    public ResponseEntity<ModeratorDTO> getModerator(@PathVariable("id") UUID id) {
        try {
            Moderator moderator = userService.getModerator(id);
            return ResponseEntity.ok().body(new ModeratorDTO(moderator));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/administrator/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.get.administrator", description = "Time taken to execute a get")
    @Counted(value = "user.endpoint.get.administrator.error", recordFailuresOnly = true)
    public AdministratorDTO getAdministrator(@PathVariable("id") UUID id) {
        try {
            return new AdministratorDTO(userService.getAdministrator(id));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }



    @PutMapping("/customer/{id}/activate")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.put.activate.customer", description = "Time taken to execute a put")
    @Counted(value = "user.endpoint.put.activate.customer.error", recordFailuresOnly = true)
    public CustomerDTO setActive(@PathVariable("id") UUID id) {
        try {
            return new CustomerDTO((Customer) userService.changeActiveCustomer(id, true, false));
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/customer/{id}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.put.deactivate.customer", description = "Time taken to execute a get")
    @Counted(value = "user.endpoint.put.deactivate.customer.error", recordFailuresOnly = true)
    public CustomerDTO setDeactivate(@PathVariable("id") UUID id) {
        try {
            return new CustomerDTO((Customer) userService.changeActiveCustomer(id, false, false));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/administrator")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.put.administrator", description = "Time taken to execute a put")
    @Counted(value = "user.endpoint.put.administrator.error", recordFailuresOnly = true)
    public AdministratorDTO putAdministrator(@RequestBody NewAdministratorDTO newAdministrator) {
        try {
            if (newAdministrator.getUsername().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
            return new AdministratorDTO(userService.createAdministrator(new Administrator(newAdministrator.username,
                newAdministrator.password)));
        } catch (WrongParametersException | DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/moderator")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.put.moderator", description = "Time taken to execute a put")
    @Counted(value = "user.endpoint.put.moderator.error", recordFailuresOnly = true)
    public ModeratorDTO putModerator(@RequestBody NewCustomerAndModeratorDTO newModerator) {
        try {
            if (newModerator.getUsername().isEmpty() || newModerator.getEmail().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
            return new ModeratorDTO(userService.createModerator(new Moderator(newModerator.username,
                newModerator.password, newModerator.email)));
        } catch (WrongParametersException | DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/customer")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.put.customer", description = "Time taken to execute a put")
    @Counted(value = "user.endpoint.put.customer.error", recordFailuresOnly = true)
    public CustomerDTO putCustomer(@RequestBody NewCustomerAndModeratorDTO newCustomer) {
        try {
            if (newCustomer.getUsername().isEmpty() || newCustomer.getEmail().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
            return new CustomerDTO(userService.createCustomer(new Customer(newCustomer.username, newCustomer.password,
                newCustomer.email)));
        } catch (WrongParametersException | DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/customer/update")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.update.customer", description = "Time taken to execute a update")
    @Counted(value = "user.endpoint.update.customer.error", recordFailuresOnly = true)
    public CustomerDTO updateCustomer(@RequestBody UpdateCustomerDTO newCustomer) {
        try {
            if (newCustomer.username.isEmpty() || newCustomer.email.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
            UUID id = newCustomer.id;
            Customer user = userService.getCustomer(id);
            user.setEmail(newCustomer.email);
            user.setUsername(newCustomer.username);
            return new CustomerDTO((Customer) userService.updateUser(user));
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/passwordChange")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.changePassword", description = "Time taken to execute a password change")
    @Counted(value = "user.endpoint.changePassword.error", recordFailuresOnly = true)
    public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            System.out.println(changePasswordDTO.oldPassword + " " + changePasswordDTO.newPassword);
            userService.changePassword(changePasswordDTO.oldPassword ,changePasswordDTO.newPassword);
        } catch (UnmachingPasswordsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/moderator/update")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.update.moderator", description = "Time taken to execute a update")
    @Counted(value = "user.endpoint.update.moderator.error", recordFailuresOnly = true)
    public ModeratorDTO updateModerator(@RequestBody ModeratorDTO newModerator, HttpServletRequest request) {
        String jws = request.getHeader("If-Match");
        if (jws == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            if (newModerator.getEmail().isEmpty() || newModerator.getUsername().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
            UUID id = newModerator.moderatorID;
            Moderator user = userService.getModerator(id);
            user.setEmail(newModerator.getEmail());
            user.setUsername(newModerator.getUsername());
            return new ModeratorDTO((Moderator) userService.updateUser(user));
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Timed(value = "user.endpoint.login", description = "Time taken to execute login")
    @Counted(value = "user.endpoint.login.error", recordFailuresOnly = true)
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        Authentication authentication;
        try {
            authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        } catch (LockedException le) {
            throw new ResponseStatusException(HttpStatus.LOCKED);
        } catch (DisabledException de) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (AuthenticationException ae) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        User user = (User) authentication.getPrincipal();
        if (user.getType() == CustomerType.CUSTOMER) {
            if (!((Customer) user).isActive()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jwt", jwtProvider.generateJWT(user.getUsername(), user.getType().name()));
        jsonObject.put("role", user.getType().name());
        return ResponseEntity.ok(jsonObject.toString());
    }
}
