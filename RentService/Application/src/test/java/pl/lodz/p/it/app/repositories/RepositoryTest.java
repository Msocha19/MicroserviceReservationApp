package pl.lodz.p.it.app.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.lodz.p.it.model.products.ProductEnt;
import pl.lodz.p.it.model.products.SkiBootEnt;
import pl.lodz.p.it.model.products.SkiEnt;
import pl.lodz.p.it.repository.ProductRepository;
import pl.lodz.p.it.repository.ClientRepository;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class RepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ClientRepository userRepository;

    @Test
    public void findProductEntByProductIDAndTypeTest() {
        ProductEnt productEnt = productRepository.findProductEntByProductIDAndType(UUID.fromString("ae8c5861-3c46-44d4-a090-115d4926395f"), "SKI");
        Assertions.assertNotNull(productEnt);
        Assertions.assertEquals(productEnt.getProductID(), UUID.fromString("ae8c5861-3c46-44d4-a090-115d4926395f"));
        Assertions.assertEquals(productEnt.getType(), "SKI");
        Assertions.assertEquals(productEnt.getPrice(), 980.125);
        Assertions.assertEquals(((SkiEnt) productEnt).getLength() , 12);
        Assertions.assertEquals(((SkiEnt) productEnt).getWeight() , 12);

        productEnt = productRepository.findProductEntByProductIDAndType(UUID.fromString("b86866d7-6210-4d69-afa4-b564594c711a"), "SKIBOOT");
        Assertions.assertNotNull(productEnt);
        Assertions.assertEquals(productEnt.getProductID(), UUID.fromString("b86866d7-6210-4d69-afa4-b564594c711a"));
        Assertions.assertEquals(productEnt.getType(), "SKIBOOT");
        Assertions.assertEquals(productEnt.getPrice(), 80.99);
        Assertions.assertEquals(((SkiBootEnt) productEnt).getSize() , 45);

        Assertions.assertNull(productRepository.findProductEntByProductIDAndType(UUID.fromString("20972ac3-eba1-465b-989e-dcf09488adb4"), "SKI"));
        Assertions.assertNull(productRepository.findProductEntByProductIDAndType(UUID.fromString("ae8c5861-3c46-44d4-a090-115d4926395f"), "SKIBOOT"));
    }
}
