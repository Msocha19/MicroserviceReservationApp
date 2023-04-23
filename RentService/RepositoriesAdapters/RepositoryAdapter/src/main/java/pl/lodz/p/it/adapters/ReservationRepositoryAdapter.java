package pl.lodz.p.it.adapters;

import pl.lodz.p.it.adapters.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.model.ClientEnt;
import pl.lodz.p.it.model.ReservationEnt;
import pl.lodz.p.it.ports.repository.reservation.InReservationPort;
import pl.lodz.p.it.ports.repository.reservation.OutReservationPort;
import pl.lodz.p.it.repository.ProductRepository;
import pl.lodz.p.it.repository.ReservationRepository;
import pl.lodz.p.it.repository.ClientRepository;
import java.util.List;
import java.util.UUID;

@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReservationRepositoryAdapter implements InReservationPort, OutReservationPort {
    @Autowired
    ReservationRepository reservationEntRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ClientRepository userRepository;

    @Autowired
    Mapper mapper;

    @Override
    public Reservation getReservation(UUID id) {
        ReservationEnt reservationEnt = reservationEntRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.mapEntToDomain(reservationEnt, true);
    }

    @Override
    public Reservation modifyReservation(Reservation reservation) {
        ClientEnt customerEnt = userRepository.findById(reservation.getCustomer()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ReservationEnt reservationEnt = reservationEntRepository.findById(reservation.getReservationID()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        reservationEnt.setClient(customerEnt);
        reservationEnt.setProduct(productRepository.findById(reservation.getProduct()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        reservationEnt.setEndDate(reservation.getEndDate());
        reservationEnt.setStartDate(reservation.getStartDate());
        ReservationEnt res = reservationEntRepository.save(reservationEnt);
        return mapper.mapEntToDomain(res, true);
    }

    @Override
    public Reservation createReservation(Reservation reservation) throws DataIntegrityViolationException {
        ReservationEnt reservationEnt = reservationEntRepository.save(mapper.mapDomainToEnt(reservation, false));
        return mapper.mapEntToDomain(reservationEnt, true);
    }

    @Override
    public void deleteReservation(UUID id) {
        reservationEntRepository.deleteById(id);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationEntRepository
                .findAll()
                .stream()
                .map(r -> mapper.mapEntToDomain(r, true))
                .toList();
    }
}
