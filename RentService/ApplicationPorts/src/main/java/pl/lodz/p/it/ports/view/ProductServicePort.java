package pl.lodz.p.it.ports.view;

import com.nimbusds.jose.JOSEException;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.domain.model.products.Product;
import pl.lodz.p.it.domain.model.products.Ski;
import pl.lodz.p.it.domain.model.products.SkiBoot;

import java.util.List;
import java.util.UUID;

@Component
public interface ProductServicePort {
    Ski createSki(Ski ski);

    SkiBoot createSkiBoot(SkiBoot skiBoot);

    void delete (UUID id);

    Product modify (String jws, Product product)  throws Exception ;

    Ski modify(Ski ski);

    SkiBoot modify(SkiBoot skiBoot);

    Product modify(Product product)  throws Exception ;

    Product get (UUID id);

    List<Product> getAll();

    List<Reservation> getReservations(UUID id);

    List<Reservation> getFutureReservations(UUID id);

   List<Reservation> getPastReservation(UUID id);

    void switchProductReservations(UUID product1, UUID product2, UUID reservation);

    Product addReservation(UUID id, UUID reservationId);

    Product removeReservation(UUID id, UUID reservationId);
}
