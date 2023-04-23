package pl.lodz.p.it.adapters.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pl.lodz.p.it.domain.model.Administrator;
import pl.lodz.p.it.domain.model.Customer;
import pl.lodz.p.it.domain.model.Moderator;
import pl.lodz.p.it.domain.model.User;
import pl.lodz.p.it.model.users.AdministratorEnt;
import pl.lodz.p.it.model.users.CustomerEnt;
import pl.lodz.p.it.model.users.CustomerTypeEnt;
import pl.lodz.p.it.model.users.ModeratorEnt;
import pl.lodz.p.it.model.users.UserEnt;
import pl.lodz.p.it.repository.UserRepository;


@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MapperImpl implements Mapper {

    @Autowired
    UserRepository userRepository;

    public UserEnt mapDomainToEnt(User user, boolean useID) {
        UserEnt userEnt;
        switch (user.getType()) {
            case MODERATOR ->
                userEnt = new ModeratorEnt(user.getUsername(), user.getPassword(), CustomerTypeEnt.MODERATOR, ((Moderator)user).getEmail());

            case ADMINISTRATOR ->
                userEnt = new AdministratorEnt(user.getUsername(), user.getPassword(), CustomerTypeEnt.ADMINISTRATOR);

            default ->
                userEnt = new CustomerEnt(user.getUsername(), user.getPassword(), CustomerTypeEnt.CUSTOMER, ((Customer)user).getEmail());
        }

        if (useID)
            userEnt.setUserID(user.getUserID());

        return userEnt;
    }

    public User mapEntToDomain(UserEnt u, boolean useID) {
        User user;
        switch (u.getType()) {
            case MODERATOR ->
                user = new Moderator(u.getUsername(), u.getPassword(), ((ModeratorEnt)u).getEmail());

            case ADMINISTRATOR ->
                user = new Administrator(u.getUsername(), u.getPassword());

            default ->
                user = new Customer(u.getUsername(), u.getPassword(), ((CustomerEnt)u).getEmail(), ((CustomerEnt) u).isActive());
        }
        if (useID)
            user.setUserID(u.getUserID());
        return user;
    }
}




