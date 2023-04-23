package pl.lodz.p.it.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.it.model.ReservationEnt;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<ReservationEnt, UUID> {
}
