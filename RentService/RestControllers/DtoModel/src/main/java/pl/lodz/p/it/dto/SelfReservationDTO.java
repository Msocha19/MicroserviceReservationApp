package pl.lodz.p.it.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SelfReservationDTO {

    public LocalDate startDate;
    public LocalDate endDate;

    public UUID product;

    public SelfReservationDTO(LocalDate startDate, LocalDate endDate, UUID product) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.product = product;
    }

}
