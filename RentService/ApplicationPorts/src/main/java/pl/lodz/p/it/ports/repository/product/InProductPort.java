package pl.lodz.p.it.ports.repository.product;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.products.Product;
import pl.lodz.p.it.domain.model.products.Ski;
import pl.lodz.p.it.domain.model.products.SkiBoot;

import java.util.UUID;

@Component
public interface InProductPort {

    void removeReservation(UUID id, UUID reservationID);

    void addReservation(UUID id, UUID reservationID);

    Ski createSki(Ski ski);

    SkiBoot createSkiBoot(SkiBoot skiBoot);

    Product createProduct(Product product) throws DataIntegrityViolationException;

    Product modifyProduct(Product product);

    Ski modifySki(Ski ski);

    SkiBoot modifySkiBoot(SkiBoot skiBoot);

    void deleteProduct(UUID id);
}
