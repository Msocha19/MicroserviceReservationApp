package pl.lodz.p.it.controllers;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import pl.lodz.p.it.domain.exceptions.WrongParametersException;
import pl.lodz.p.it.domain.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.domain.model.products.Product;
import pl.lodz.p.it.domain.model.products.Ski;
import pl.lodz.p.it.domain.model.products.SkiBoot;
import pl.lodz.p.it.dto.ReservationDTO;
import pl.lodz.p.it.dto.SkiBootDTO;
import pl.lodz.p.it.dto.SkiDTO;
import pl.lodz.p.it.ports.view.ProductServicePort;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/product")
public class ProductEndpoint {

	@Autowired
	private ProductServicePort productService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Product> getAll () {
		return productService.getAll();
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "product.endpoint.get.product", description = "Time taken to execute a get")
	@Counted(value = "product.endpoint.get.product.error", recordFailuresOnly = true)
	public ResponseEntity<Product> get (@PathVariable("id") UUID id) {
		try {
			Product product = productService.get(id);
			return ResponseEntity.ok().body(product);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatusCode());
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}

	@GetMapping("/{id}/reservations")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "product.endpoint.get.reservations", description = "Time taken to execute a get")
	@Counted(value = "product.endpoint.get.reservations.error", recordFailuresOnly = true)
	public List<ReservationDTO> getReservations (@PathVariable("id") UUID id, @Param("past") boolean past) {
		try {
			Product product = productService.get(id);
			List<Reservation> res;
			if (past)
				res = productService.getPastReservation(product.getProductID());
			else
				res = productService.getFutureReservations(product.getProductID());
			List<ReservationDTO> resDTO = new ArrayList<>();
			for (Reservation r : res) {
				resDTO.add(new ReservationDTO(r));
			}
			return resDTO;
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatusCode());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}

	@PutMapping("/ski")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "product.endpoint.put.ski", description = "Time taken to execute a put")
	@Counted(value = "product.endpoint.put.ski.error", recordFailuresOnly = true)
	public SkiDTO putSki (@RequestBody SkiDTO ski) {
		try {
			return new SkiDTO(productService.createSki(new Ski(ski.price, ski.weight, ski.length)));
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatusCode());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}

	@PutMapping("/skiboot")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "product.endpoint.put.skiBoot", description = "Time taken to execute a put")
	@Counted(value = "product.endpoint.put.skiskiBoot.error", recordFailuresOnly = true)
	public SkiBootDTO putSkiBoot (@RequestBody SkiBootDTO skiBoot) {
		try {
			return new SkiBootDTO(productService.createSkiBoot(new SkiBoot(skiBoot.price, skiBoot.size)));
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "product.endpoint.delete", description = "Time taken to execute a delete")
	@Counted(value = "product.endpoint.delete.error", recordFailuresOnly = true)
	public void delete (@PathVariable("id") UUID id) {
		try {
			productService.delete(id);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatusCode());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}

	@PutMapping("/update/ski")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "product.endpoint.update.ski", description = "Time taken to execute a update")
	@Counted(value = "product.endpoint.update.ski.error", recordFailuresOnly = true)
	public SkiDTO updateSki (@RequestBody SkiDTO ski) {
		try {
			Ski s = productService.modify(new Ski(ski.productID, ski.price, ski.getReservations(), ski.weight, ski.length));
			return new SkiDTO(s);
		} catch (WrongParametersException e) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatusCode());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}

	@PutMapping("/update/skiboot")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "product.endpoint.update.skiBoot", description = "Time taken to execute a update")
	@Counted(value = "product.endpoint.update.skiBoot.error", recordFailuresOnly = true)
	public SkiBootDTO updateSkiBoot (@RequestBody SkiBootDTO skiBoot) {
		try {
			SkiBoot sb = productService.modify(new SkiBoot(skiBoot.productID, skiBoot.price, skiBoot.getReservations(), skiBoot.size));
			return new SkiBootDTO(sb);
		} catch (WrongParametersException e) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatusCode());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}
}
