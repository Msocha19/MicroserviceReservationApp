package pl.lodz.p.it.controllers;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.domain.exceptions.InvalidDateException;
import pl.lodz.p.it.domain.exceptions.WrongParametersException;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.domain.exceptions.NotFoundException;
import pl.lodz.p.it.dto.ReservationDTO;
import pl.lodz.p.it.ports.view.ClientServicePort;
import pl.lodz.p.it.ports.view.ReservationServicePort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationEndpoint {

	@Autowired
	private ReservationServicePort reservationService;

	@Autowired
	private ClientServicePort clientServicePort;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "reservation.endpoint.get.all", description = "Time taken to execute a get")
	@Counted(value = "reservation.endpoint.get.all.error", recordFailuresOnly = true)
	public List<ReservationDTO> getAll () {
		List<ReservationDTO> resDTO = new ArrayList<>();
		for (Reservation r : reservationService.getAll()) {
			resDTO.add(new ReservationDTO(r));
		}
		return resDTO;
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "reservation.endpoint.get", description = "Time taken to execute a get")
	@Counted(value = "reservation.endpoint.get.error", recordFailuresOnly = true)
	public ResponseEntity<ReservationDTO> get (@PathVariable("id") UUID id) {
		try {
			ReservationDTO reservation = new ReservationDTO(reservationService.get(id));
            return ResponseEntity.ok().body(reservation);
		} catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode());
		} catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
	}

	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "reservation.endpoint.put", description = "Time taken to execute a put")
	@Counted(value = "reservation.endpoint.put.error", recordFailuresOnly = true)
	public ReservationDTO put (@RequestBody ReservationDTO r) {

		try {
			Reservation res = new Reservation(r.startDate, r.endDate, r.customer, r.product);
			return new ReservationDTO(reservationService.create(res));
		} catch (InvalidDateException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
		} catch (ResponseStatusException e) {
			e.printStackTrace();
			throw new ResponseStatusException(e.getStatusCode());
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "reservation.endpoint.delete", description = "Time taken to execute a delete")
	@Counted(value = "reservation.endpoint.delete.error", recordFailuresOnly = true)
	public void delete (@PathVariable("id") UUID id) {
        this.deleteIf(id, false);
    }

	@DeleteMapping("/forced/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "reservation.endpoint.delete.forcefully", description = "Time taken to execute a delete")
	@Counted(value = "reservation.endpoint.delete.forcefully.error", recordFailuresOnly = true)
    public void deleteForce (@PathVariable("id") UUID id) {
        this.deleteIf(id, true);
	}

    private void deleteIf(UUID id, boolean force) {
        try {
			reservationService.delete(id, force);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatusCode());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

	@PutMapping("/update")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "reservation.endpoint.update", description = "Time taken to execute a update")
	@Counted(value = "reservation.endpoint.update.error", recordFailuresOnly = true)
	public ReservationDTO update (@RequestBody ReservationDTO newReservation, HttpServletRequest request) {
		try {
			return new ReservationDTO(reservationService.modify(
					new Reservation(newReservation.reservationID,
							newReservation.startDate,
							newReservation.endDate,
							newReservation.customer,
							newReservation.product)));
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatusCode());
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}

	@GetMapping("/client")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "reservation.endpoint.get.customer.reservations", description = "Time taken to execute a get")
	@Counted(value = "reservation.endpoint.get.customer.reservations.error", recordFailuresOnly = true)
	public List<ReservationDTO> getCustomerReservations(@Param("id") String id, @Param("past") boolean past) {
		try {
			UUID userID;
			if (id == null) {
				userID = clientServicePort.getUserFromServerContext().getId();
			} else {
				userID = UUID.fromString(id);
			}
			List<Reservation> res;
			List<ReservationDTO> resDTO = new ArrayList<>();
			if (past)
				res = clientServicePort.getPastReservations(userID);
			else
				res = clientServicePort.getFutureReservations(userID);

			for (Reservation r : res) {
				resDTO.add(new ReservationDTO(r));
			}
			return resDTO;
		} catch (WrongParametersException e) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		}catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatusCode());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}

	@GetMapping("/product/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Timed(value = "reservation.endpoint.get.product.reservations", description = "Time taken to execute a get")
	@Counted(value = "reservation.endpoint.get.product.reservations.error", recordFailuresOnly = true)
	public List<ReservationDTO> getProduct(@PathVariable("id") UUID id) {
		try {
			List<ReservationDTO> resDTO = new ArrayList<>();
			for (Reservation r : reservationService.getProductReservations(id)) {
				resDTO.add(new ReservationDTO(r));
			}
			return resDTO;
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatusCode());
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}
}
