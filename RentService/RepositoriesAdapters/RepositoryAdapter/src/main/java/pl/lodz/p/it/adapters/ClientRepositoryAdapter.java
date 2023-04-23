package pl.lodz.p.it.adapters;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.adapters.mapper.Mapper;
import pl.lodz.p.it.domain.exceptions.DatabaseException;
import pl.lodz.p.it.domain.model.Client;
import pl.lodz.p.it.model.ClientEnt;
import pl.lodz.p.it.ports.repository.client.InClientPort;
import pl.lodz.p.it.ports.repository.client.OutClientPort;
import pl.lodz.p.it.repository.ProductRepository;
import pl.lodz.p.it.repository.ReservationRepository;
import pl.lodz.p.it.repository.ClientRepository;
import java.util.*;

@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ClientRepositoryAdapter implements InClientPort, OutClientPort {

    @Autowired
    ClientRepository userEntRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    Mapper mapper;

    @Override
    public Client createClient(Client user) throws DatabaseException {
        try {
            ClientEnt userEnt = userEntRepository.save(mapper.mapDomainToEnt(user, false));
            return mapper.mapEntToDomain(userEnt, false);
        } catch (DataAccessException | JDBCConnectionException e) {
            throw new DatabaseException("Could not create client", e.getCause());
        }
    }

    @Override
    public Client getUser(UUID id) {
        ClientEnt userEnt = userEntRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.mapEntToDomain(userEnt, true);
    }

    @Override
    public List<Client> getAllUsers() {
        return userEntRepository
                .findAll()
                .stream()
                .map(userEnt -> mapper.mapEntToDomain(userEnt, true))
                .toList();
    }

    @Override
    public void removeReservation(UUID id, UUID reservationID) {
        ClientEnt clientEnt = userEntRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        clientEnt.getReservations().remove(reservationRepository.findById(reservationID)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        userEntRepository.save(clientEnt);
    }

    @Override
    public void addReservation(UUID id, UUID reservationID) {
        ClientEnt clientEnt = userEntRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        clientEnt.getReservations().add(reservationRepository.findById(reservationID)
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        userEntRepository.save(clientEnt);
    }

    @Override
    public void changeClientActivityClient(UUID id, boolean active) throws DatabaseException {
        try {
        ClientEnt clientEnt = userEntRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (clientEnt.isActive() == active) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
        clientEnt.setActive(active);
            userEntRepository.save(clientEnt);
        } catch (DataAccessException | JDBCConnectionException e) {
            throw new DatabaseException("Could not change activity of a Client!", e.getCause());
        }
    }
}
