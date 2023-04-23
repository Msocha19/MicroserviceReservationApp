package pl.lodz.p.it.ports.repository.product;

import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.products.Product;
import pl.lodz.p.it.domain.model.products.Ski;
import pl.lodz.p.it.domain.model.products.SkiBoot;

import java.util.List;
import java.util.UUID;

@Component
public interface OutProductPort {

    Product getProduct(UUID id);

    List<Product> getAllProducts();

    SkiBoot findSkiBoot(UUID id);

    Ski findSki(UUID id);
}
