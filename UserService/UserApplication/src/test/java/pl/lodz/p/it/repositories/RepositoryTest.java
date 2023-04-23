package pl.lodz.p.it.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.lodz.p.it.model.users.CustomerEnt;
import pl.lodz.p.it.model.users.CustomerTypeEnt;
import pl.lodz.p.it.model.users.ModeratorEnt;
import pl.lodz.p.it.model.users.UserEnt;
import pl.lodz.p.it.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class RepositoryTest {

    @Autowired
    UserRepository userRepository;


    @Test
    public void findUserEntByUsernameAndTypeTest() {
        List<UserEnt> userEnt = userRepository.findUserEntByUsernameAndType("customer2", CustomerTypeEnt.CUSTOMER);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 1);
        Assertions.assertEquals(userEnt.get(0).getUserID(), UUID.fromString("02171fb3-d462-4bd7-9bd0-4ca635e5c9fb"));
        Assertions.assertEquals(userEnt.get(0).getUsername(), "customer2");
        Assertions.assertEquals(((CustomerEnt) userEnt.get(0)).getEmail(), "c2@xyz.pl");
        Assertions.assertEquals(((CustomerEnt) userEnt.get(0)).isActive(), false);

        userEnt = userRepository.findUserEntByUsernameAndType("admin1", CustomerTypeEnt.ADMINISTRATOR);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 1);
        Assertions.assertEquals(userEnt.get(0).getUserID(), UUID.fromString("19d5503b-1eee-4949-9f2d-e8c5f44eb4c9"));
        Assertions.assertEquals(userEnt.get(0).getUsername(), "admin1");

        userEnt = userRepository.findUserEntByUsernameAndType("moderator1", CustomerTypeEnt.MODERATOR);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 1);
        Assertions.assertEquals(userEnt.get(0).getUserID(), UUID.fromString("9b49fcbb-cf38-4db2-b279-bcc02ff154fa"));
        Assertions.assertEquals(userEnt.get(0).getUsername(), "moderator1");
        Assertions.assertEquals(((ModeratorEnt) userEnt.get(0)).getEmail(), "mod1@app.xyz");

        userEnt = userRepository.findUserEntByUsernameAndType("cus", CustomerTypeEnt.CUSTOMER);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 0);

        userEnt = userRepository.findUserEntByUsernameAndType("customer2", CustomerTypeEnt.MODERATOR);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 0);
    }

    @Test
    public void findUserEntByUsernameContainingAndTypeTest() {
        List<UserEnt> userEnt = userRepository.findUserEntByUsernameContainingAndType("cus", CustomerTypeEnt.CUSTOMER);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 3);
        Assertions.assertNotNull(((CustomerEnt) userEnt.get(0)).getEmail());
        Assertions.assertNotNull(((CustomerEnt) userEnt.get(0)).isActive());

        userEnt = userRepository.findUserEntByUsernameContainingAndType("admin", CustomerTypeEnt.ADMINISTRATOR);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 3);

        userEnt = userRepository.findUserEntByUsernameContainingAndType("mod", CustomerTypeEnt.MODERATOR);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 4);
        Assertions.assertNotNull(((ModeratorEnt) userEnt.get(0)).getEmail());

        userEnt = userRepository.findUserEntByUsernameContainingAndType("jon", CustomerTypeEnt.CUSTOMER);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 1);
        Assertions.assertEquals(((CustomerEnt) userEnt.get(0)).getEmail(), "cj@xyz.pl");
        Assertions.assertTrue(((CustomerEnt) userEnt.get(0)).isActive());

        userEnt = userRepository.findUserEntByUsernameContainingAndType("jon", CustomerTypeEnt.MODERATOR);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 1);
        Assertions.assertEquals(((ModeratorEnt) userEnt.get(0)).getEmail(), "mj@xyz.pl");

        userEnt = userRepository.findUserEntByUsernameContainingAndType("cus", CustomerTypeEnt.ADMINISTRATOR);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 0);

        userEnt = userRepository.findUserEntByUsernameContainingAndType("", CustomerTypeEnt.MODERATOR);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 4);
    }

    @Test
    public void findUserEntByTypeTest() {
        List<UserEnt> userEnt = userRepository.findUserEntByType(CustomerTypeEnt.CUSTOMER);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 5);
        Assertions.assertNotNull(((CustomerEnt) userEnt.get(0)).getEmail());
        Assertions.assertNotNull(((CustomerEnt) userEnt.get(0)).isActive());

        userEnt = userRepository.findUserEntByType(CustomerTypeEnt.ADMINISTRATOR);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 4);

        userEnt = userRepository.findUserEntByType(CustomerTypeEnt.MODERATOR);
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.size(), 4);
        Assertions.assertNotNull(((ModeratorEnt) userEnt.get(0)).getEmail());
    }

    @Test
    public void findUserEntByUsernameTest() {
        UserEnt userEnt = userRepository.findUserEntByUsername("customer2");
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.getUserID(), UUID.fromString("02171fb3-d462-4bd7-9bd0-4ca635e5c9fb"));
        Assertions.assertEquals(userEnt.getUsername(), "customer2");
        Assertions.assertEquals(((CustomerEnt) userEnt).getEmail(), "c2@xyz.pl");
        Assertions.assertEquals(((CustomerEnt) userEnt).isActive(), false);

        userEnt = userRepository.findUserEntByUsername("admin1");
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.getUserID(), UUID.fromString("19d5503b-1eee-4949-9f2d-e8c5f44eb4c9"));
        Assertions.assertEquals(userEnt.getUsername(), "admin1");

        userEnt = userRepository.findUserEntByUsername("moderator1");
        Assertions.assertNotNull(userEnt);
        Assertions.assertEquals(userEnt.getUserID(), UUID.fromString("9b49fcbb-cf38-4db2-b279-bcc02ff154fa"));
        Assertions.assertEquals(userEnt.getUsername(), "moderator1");
        Assertions.assertEquals(((ModeratorEnt) userEnt).getEmail(), "mod1@app.xyz");

        userEnt = userRepository.findUserEntByUsername("cus");
        Assertions.assertNull(userEnt);

        userEnt = userRepository.findUserEntByUsername("");
        Assertions.assertNull(userEnt);
    }
}
