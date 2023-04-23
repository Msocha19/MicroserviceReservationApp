package pl.lodz.p.it.model.products;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.model.ReservationEnt;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name="products")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "p")
public abstract class ProductEnt {

    @Id
    @Column(name = "productID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productID;

    @Version
    private Integer version;

    @Column
    private double price;

    @Column
    private String type;

    @OneToMany(cascade = CascadeType.REMOVE ,fetch = FetchType.EAGER, mappedBy = "product")
    private List<ReservationEnt> reservations = new ArrayList<>();

    public ProductEnt(double price, String type) {
        this.price = price;
        this.type = type;
    }

    public ProductEnt(UUID productID, double price, List<ReservationEnt> reservations, String type) {
        this.productID = productID;
        this.price = price;
        this.reservations = reservations;
        this.type = type;
    }

    public ProductEnt(double price, List<ReservationEnt> reservations, String type) {
        this.price = price;
        this.reservations = reservations;
        this.type = type;
    }
}
