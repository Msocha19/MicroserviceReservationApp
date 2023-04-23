package pl.lodz.p.it.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.domain.model.products.Ski;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class SkiDTO {

    public UUID productID;
    public double price;
    public List<Reservation> reservations;
    public double weight;
    public double length;

    public SkiDTO(Ski ski) {
        this.productID = ski.getProductID();
        this.price = ski.getPrice();
        this.reservations = ski.getReservations();
        this.weight = ski.getWeight();
        this.length = ski.getLength();
    }
}
