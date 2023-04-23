package pl.lodz.p.it.dto;

import lombok.NoArgsConstructor;
import pl.lodz.p.it.domain.model.Reservation;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
public class ReservationDTO {

    public UUID reservationID;
    public LocalDate startDate;
    public LocalDate endDate;
    public UUID customer;
    public UUID product;

    public ReservationDTO(Reservation reservation) {
        this.reservationID = reservation.getReservationID();
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.customer = reservation.getCustomer();
        this.product = reservation.getProduct();
    }


}
