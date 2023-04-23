package pl.lodz.p.it.domain.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    private UUID reservationID;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID customer;
    private UUID product;

    public Reservation(LocalDate startDate, LocalDate endDate, UUID customer, UUID product) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.customer = customer;
        this.product = product;
    }
}
