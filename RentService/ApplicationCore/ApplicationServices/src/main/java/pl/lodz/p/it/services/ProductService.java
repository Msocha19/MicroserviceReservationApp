package pl.lodz.p.it.services;

import com.nimbusds.jose.JOSEException;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.domain.model.products.Product;
import pl.lodz.p.it.domain.model.products.Ski;
import pl.lodz.p.it.domain.model.products.SkiBoot;
import pl.lodz.p.it.ports.view.ProductServicePort;
import pl.lodz.p.it.domain.model.Reservation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lodz.p.it.ports.repository.product.InProductPort;
import pl.lodz.p.it.ports.repository.product.OutProductPort;
import pl.lodz.p.it.ports.repository.reservation.OutReservationPort;

import java.time.LocalDate;
import java.util.*;

@Service
public class ProductService implements ProductServicePort {

	@Autowired
	private InProductPort inProductPort;

	@Autowired
	private OutProductPort outProductPort;

	@Autowired
	private OutReservationPort outReservationPort;

	@Override
	synchronized public Ski createSki(Ski ski) {
		return inProductPort.createSki(ski);
	}

	@Override
	synchronized public SkiBoot createSkiBoot(SkiBoot skiBoot) {
		return inProductPort.createSkiBoot(skiBoot);
	}

	@Override
	@Timed(value = "product.deletion.time", description = "Time taken to delete a product")
	public void delete (UUID id) {
		Product product = outProductPort.getProduct(id);
		if (!product.isReserved() && this.getFutureReservations(id).isEmpty())
			inProductPort.deleteProduct(id);
		else
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
	}

	@Override
	@Timed(value = "product.modify.time", description = "Time taken to modify a product")
	public Product modify (String jws, Product product) throws Exception {
		return inProductPort.modifyProduct(product);
	}

	@Override
	@Timed(value = "ski.modify.time", description = "Time taken to modify a ski")
	public Ski modify(Ski ski) {
		return inProductPort.modifySki(ski);
	}

	@Override
	@Timed(value = "skiBoot.modify.time", description = "Time taken to modify a skiBoot")
	public SkiBoot modify(SkiBoot skiBoot) {
		return inProductPort.modifySkiBoot(skiBoot);
	}

	@Override
	@Timed(value = "skiBoot.modify.time", description = "Time taken to modify a skiBoot")
	public Product modify(Product product) {
        return inProductPort.modifyProduct(product);
    }

	@Override
	public Product get (UUID id) {
		Product p = outProductPort.getProduct(id);
        if (p == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return p;
	}

	@Override
	@Counted(value = "get.all.products")
	public List<Product> getAll() {
		return new ArrayList<>(outProductPort.getAllProducts());
	}

	@Override
	@Counted(value = "get.products.reservations")
	public List<Reservation> getReservations(UUID id) {
		return outProductPort.getProduct(id).getReservations();
	}

	@Override
	@Counted(value = "get.products.future.reservations")
	public List<Reservation> getFutureReservations(UUID id) {
        return outProductPort.getProduct(id).getReservations().stream().filter(r -> r.getEndDate().isAfter(LocalDate.now())).toList();
	}

	@Override
	@Counted(value = "get.products.past.reservations")
	public List<Reservation> getPastReservation(UUID id) {
        return outProductPort.getProduct(id).getReservations().stream().filter(r -> r.getEndDate().isBefore(LocalDate.now())).toList();
    }

	@Override
	public void switchProductReservations(UUID product1, UUID product2, UUID reservation) {
		inProductPort.removeReservation(product1, reservation);
		inProductPort.addReservation(product2, reservation);
	}

	@Override
	@Timed(value = "product.creation.time", description = "Time taken to create a product")
	public Product addReservation(UUID id, UUID reservationId) {
		inProductPort.addReservation(id, reservationId);
		return outProductPort.getProduct(id);
	}

	@Override
	@Timed(value = "product.reservation.deletion.time", description = "Time taken to delete a reservation from product")
	public Product removeReservation(UUID id, UUID reservationId) {
		inProductPort.removeReservation(id, reservationId);
		return outProductPort.getProduct(id);
	}
}