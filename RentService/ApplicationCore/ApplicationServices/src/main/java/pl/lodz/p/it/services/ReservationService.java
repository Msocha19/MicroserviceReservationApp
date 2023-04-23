package pl.lodz.p.it.services;

import com.nimbusds.jose.JOSEException;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.domain.exceptions.InactiveClientException;
import pl.lodz.p.it.domain.model.products.Product;
import pl.lodz.p.it.ports.view.ReservationServicePort;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.domain.exceptions.InvalidDateException;
import pl.lodz.p.it.ports.repository.reservation.InReservationPort;
import pl.lodz.p.it.ports.repository.reservation.OutReservationPort;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReservationService implements ReservationServicePort {

	@Autowired
	private InReservationPort inReservationPort;

	@Autowired
	private OutReservationPort outReservationPort;

    @Autowired
	private ProductService productService;

    @Autowired
    private ClientService userService;

	@Override
	@Timed(value = "reservation.creation.time", description = "Time taken to create a reservation")
	@Counted(value = "reservation.creation.error.counter", recordFailuresOnly = true)
	public synchronized Reservation create (Reservation r) throws Exception {
		if (r.getStartDate().isBefore(LocalDate.now()) || r.getEndDate().isBefore(LocalDate.now()))
			throw new InvalidDateException("cannot make reservation in the past");

		if (r.getEndDate().isBefore(r.getStartDate()))
			throw new InvalidDateException("end date cannot be before start date");

		if (!(userService.getClientById(r.getCustomer())).isActive())
			throw new InactiveClientException();

		Product product = productService.get(r.getProduct());
		if (product.isReserved(r.getStartDate(), r.getEndDate()))
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        return inReservationPort.createReservation(r);
    }

	@Override
	@Timed(value = "reservation.deletion.time", description = "Time taken to delete a reservation")
	@Counted(value = "reservation.deletion.error.counter", recordFailuresOnly = true)
	public void delete (UUID id, boolean force) {
		Reservation reservation = outReservationPort.getReservation(id);
        if ((reservation.getStartDate().isBefore(LocalDate.now()) || (reservation.getStartDate().isEqual(LocalDate.now()))
                    && reservation.getEndDate().isAfter(LocalDate.now())) && !force)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		inReservationPort.deleteReservation(id);
	}

	@Override
	@Timed(value = "reservation.modify.time", description = "Time taken to modify a reservation")
	public Reservation modify (Reservation reservation) throws Exception {
		return inReservationPort.modifyReservation(reservation);
	}

	@Override
	@Counted(value = "get.reservation.byId")
	public Reservation get (UUID id) {
		return outReservationPort.getReservation(id);
	}

	@Override
	@Counted(value = "get.all.reservation")
	public List<Reservation> getAll() {
        return new ArrayList<>(outReservationPort.getAllReservations());
	}

	@Override
	@Counted(value = "get.client.reservations")
	public List<Reservation> getCustomerReservations(UUID  id) {
		return userService.getClientById(id).getReservations();
	}

	@Override
	@Counted(value = "get.product.reservations")
	public List<Reservation> getProductReservations(UUID id) {
		return productService.get(id).getReservations();
	}
}
