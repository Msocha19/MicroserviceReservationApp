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
@DiscriminatorValue("SkiBoot")
@NoArgsConstructor
public class SkiBootEnt extends ProductEnt {

    @Column
    private double size;

    public SkiBootEnt(double prize, double size) {
        super(prize, "SKIBOOT");
        this.size = size;
    }

    public SkiBootEnt(UUID id, double prize, List<ReservationEnt> reservations, double size) {
        super(id, prize, reservations, "SKIBOOT");
        this.size = size;
    }

    public SkiBootEnt(double prize, List<ReservationEnt> reservations, double size) {
        super(prize, reservations, "SKIBOOT");
        this.size = size;
    }
}
