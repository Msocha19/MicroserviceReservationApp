package pl.lodz.p.it.ports.repository.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.User;

import java.util.UUID;

@Component
public interface InUserPort {

    User createUser(User user) throws DataIntegrityViolationException;

    User modifyUser(User user);

    void deleteUser(UUID id);
}
