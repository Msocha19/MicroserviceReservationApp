package pl.lodz.p.it.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.model.products.ProductEnt;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEnt, UUID> {
    ProductEnt findProductEntByProductIDAndType(UUID id, String type);
}
