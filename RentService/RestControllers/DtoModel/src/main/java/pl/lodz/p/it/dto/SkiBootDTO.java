package pl.lodz.p.it.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.domain.model.products.SkiBoot;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class SkiBootDTO {
    public UUID productID;
    public double price;
    public List<Reservation> reservations;
    public double size;

    public SkiBootDTO(SkiBoot skiBoot) {
        this.productID = skiBoot.getProductID();
        this.price = skiBoot.getPrice();
        this.reservations = skiBoot.getReservations();
        this.size = skiBoot.getSize();
    }
}
