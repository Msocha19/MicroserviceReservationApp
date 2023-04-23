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
public class Ski extends Product {
    private double weight;

    private double length;

    public Ski(double price, double weight, double length) {
        super(price, "SKI");
        if (price <= 0 || weight <= 0 || length <= 0) {
            throw new WrongParametersException("Values have to be positive");
        }
        this.weight = weight;
        this.length = length;
    }

    public Ski(UUID id, double price, List<Reservation> reservationList, double weight, double length) {
        super(id, price, reservationList, "SKI");
        if (price <= 0 || weight <= 0 || length <= 0) {
            throw new WrongParametersException("Values have to be positive");
        }
        this.weight = weight;
        this.length = length;
    }

    public Ski(double price, List<Reservation> reservationList, double weight, double length) {
        super(price, reservationList, "SKI");
        if (price <= 0 || weight <= 0 || length <= 0) {
            throw new WrongParametersException("Values have to be positive");
        }
        this.weight = weight;
        this.length = length;
    }
}
