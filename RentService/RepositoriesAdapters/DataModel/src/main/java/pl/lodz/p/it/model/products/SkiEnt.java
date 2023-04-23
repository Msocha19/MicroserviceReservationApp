package pl.lodz.p.it.model.products;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.model.ReservationEnt;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@DiscriminatorValue("Ski")
@NoArgsConstructor
public class SkiEnt extends ProductEnt {

    @Column
    private double weight;

    @Column
    private double length;

    public SkiEnt(double price, double weight, double length) {
        super(price, "SKI");
        this.weight = weight;
        this.length = length;
    }

    public SkiEnt(UUID id, double price, List<ReservationEnt> reservationList, double weight, double length) {
        super(id, price, reservationList, "SKI");
        this.weight = weight;
        this.length = length;
    }

    public SkiEnt(double price, List<ReservationEnt> reservationList, double weight, double length) {
        super(price, reservationList, "SKI");
        this.weight = weight;
        this.length = length;
    }
}
