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
import pl.lodz.p.it.domain.model.products.Product;
import pl.lodz.p.it.domain.model.products.Ski;
import pl.lodz.p.it.domain.model.products.SkiBoot;
import pl.lodz.p.it.model.products.ProductEnt;
import pl.lodz.p.it.model.products.SkiBootEnt;
import pl.lodz.p.it.model.products.SkiEnt;
import pl.lodz.p.it.ports.repository.product.InProductPort;
import pl.lodz.p.it.ports.repository.product.OutProductPort;
import pl.lodz.p.it.repository.ProductRepository;
import pl.lodz.p.it.repository.ReservationRepository;
import pl.lodz.p.it.repository.ClientRepository;
import java.util.List;
import java.util.UUID;


@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ProductRepositoryAdapter implements InProductPort, OutProductPort {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ClientRepository userRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    Mapper mapper;

    @Override
    public Product modifyProduct(Product product) {
        Product product1 = this.getProduct(product.getProductID());
        product1.setReservations(product.getReservations());
        product1.setPrice(product.getPrice());
        ProductEnt p = productRepository.save(mapper.mapDomainToEnt(product1, true));
        return mapper.mapEntToDomain(p, true);
    }

    @Override
    public Ski modifySki(Ski ski) {
        try {
            SkiEnt s = (SkiEnt) productRepository.findProductEntByProductIDAndType(ski.getProductID(), "SKI");
            s.setLength(ski.getLength());
            s.setWeight(ski.getWeight());
            s.setPrice(ski.getPrice());
            SkiEnt skiEnt = productRepository.save(s);
            return (Ski) mapper.mapEntToDomain(skiEnt, true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public SkiBoot modifySkiBoot(SkiBoot skiBoot) {
        try {
            SkiBootEnt sb = (SkiBootEnt) productRepository.findProductEntByProductIDAndType(skiBoot.getProductID(), "SKIBOOT");
            sb.setSize(skiBoot.getSize());
            sb.setPrice(skiBoot.getPrice());
            SkiBootEnt skiBootEnt = productRepository.save(sb);
            return (SkiBoot) mapper.mapEntToDomain(skiBootEnt, true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public SkiBoot findSkiBoot(UUID id) {
        return (SkiBoot) mapper.mapEntToDomain(productRepository.findProductEntByProductIDAndType(id, "SKIBOOT"), true);
    }

    @Override
    public Ski findSki(UUID id) {
        return (Ski) mapper.mapEntToDomain(productRepository.findProductEntByProductIDAndType(id, "SKI"), true);
    }


    @Override
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    @Override
    public void removeReservation(UUID id, UUID reservationID) {
        ProductEnt productEnt = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        productEnt.getReservations().remove(reservationRepository.findById(reservationID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        productRepository.save(productEnt);
    }

    @Override
    public void addReservation(UUID id, UUID reservationID) {
        ProductEnt productEnt = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        productEnt.getReservations().add(reservationRepository.findById(reservationID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        productRepository.save(productEnt);
    }

    @Override
    public Ski createSki(Ski ski) {
        ProductEnt skiEnt = productRepository.save(mapper.mapDomainToEnt(ski, false));
        return (Ski) mapper.mapEntToDomain(skiEnt, true);
    }

    @Override
    public SkiBoot createSkiBoot(SkiBoot skiBoot) {
        ProductEnt productEnt = productRepository.save(mapper.mapDomainToEnt(skiBoot, false));
        return (SkiBoot) mapper.mapEntToDomain(productEnt, true);
    }

    @Override
    public Product createProduct(Product product) throws DataIntegrityViolationException {
        ProductEnt productEnt = productRepository.save(mapper.mapDomainToEnt(product, false));
        return mapper.mapEntToDomain(productEnt, true);
    }

    @Override
    public Product getProduct(UUID id) {
        ProductEnt productEnt = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.mapEntToDomain(productEnt, true);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productEnt -> mapper.mapEntToDomain(productEnt, true))
                .toList();
    }
}
