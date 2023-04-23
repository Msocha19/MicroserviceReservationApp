package pl.lodz.p.it.domain.model.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.domain.exceptions.WrongParametersException;
import pl.lodz.p.it.domain.model.Reservation;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkiBoot extends Product {

    private double size;

    public SkiBoot(double prize, double size) {
        super(prize, "SKIBOOT");
        if (prize <= 0 || size <= 0) {
            throw new WrongParametersException("Values have to be positive");
        }
        this.size = size;
    }

    public SkiBoot(UUID id, double prize, List<Reservation> reservations, double size) {
        super(id, prize, reservations, "SKIBOOT");
        if (prize <= 0 || size <= 0) {
            throw new WrongParametersException("Values have to be positive");
        }
        this.size = size;
    }

    public SkiBoot(double prize, List<Reservation> reservations, double size) {
        super(prize, reservations, "SKIBOOT");
        if (prize <= 0 || size <= 0) {
            throw new WrongParametersException("Values have to be positive");
        }
        this.size = size;
    }
}
