package pl.lodz.p.it.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.model.products.ProductEnt;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name="reservations")
public class ReservationEnt {

    @Id
    @Column(name = "reservationID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID reservationID;

    @Version
    private Integer version;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name="id")
    private ClientEnt client;

    @ManyToOne
    @JoinColumn(name="productID")
    private ProductEnt product;

    public ReservationEnt(LocalDate startDate, LocalDate endDate, ClientEnt client, ProductEnt product) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
        this.product = product;
    }

    public ReservationEnt(UUID reservationID, LocalDate startDate, LocalDate endDate, ClientEnt client, ProductEnt product) {
        this.reservationID = reservationID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
        this.product = product;
    }
}
