package pl.lodz.p.it.domain.model.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.domain.model.Reservation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Product {

    private UUID productID;

    private double price;

    private List<Reservation> reservations = new ArrayList<>();

    private String type;
    public Product(double price, String type) {
        this.price = price;
        this.reservations = new ArrayList<>();
        this.type = type;
    }

    public Product(double price, List<Reservation> reservations, String type) {
        this.price = price;
        this.reservations = reservations;
        this.type = type;
    }

    public boolean isReserved(LocalDate nStart, LocalDate nEnd) {
        for (Reservation reservation : reservations) {
            LocalDate rStart = reservation.getStartDate();
            LocalDate rEnd = reservation.getEndDate();
            if ((nStart.isBefore(rEnd) && nStart.isAfter(rStart))
                    || (nStart.isBefore(rEnd) && nEnd.isAfter(rEnd))
                    || (nEnd.isBefore(rEnd) && nEnd.isAfter(rStart))
                    || nStart.isEqual(rStart) || nEnd.isEqual(rStart))
                return true;
        }
        return false;
    }

    public boolean isReserved() {
        for (Reservation reservation : reservations) {
            if (reservation.getStartDate().isBefore(LocalDate.now()) && reservation.getEndDate().isAfter(LocalDate.now()))
                return true;
        }
        return false;
    }
}
