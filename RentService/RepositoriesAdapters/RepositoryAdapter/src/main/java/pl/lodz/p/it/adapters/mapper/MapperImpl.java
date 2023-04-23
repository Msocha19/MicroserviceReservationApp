package pl.lodz.p.it.adapters.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.domain.model.Client;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.model.ClientEnt;
import pl.lodz.p.it.model.products.ProductEnt;
import pl.lodz.p.it.model.ReservationEnt;
import pl.lodz.p.it.model.products.SkiBootEnt;
import pl.lodz.p.it.model.products.SkiEnt;
import pl.lodz.p.it.repository.ProductRepository;
import pl.lodz.p.it.repository.ReservationRepository;
import pl.lodz.p.it.repository.ClientRepository;
import pl.lodz.p.it.domain.model.products.*;
import java.util.List;

@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MapperImpl implements Mapper {

    @Autowired
    ReservationRepository reservationEntRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ClientRepository userRepository;

    public ReservationEnt mapDomainToEnt(Reservation r, boolean useID) {
        if (useID)
            return new ReservationEnt(r.getReservationID(), r.getStartDate(), r.getEndDate(), userRepository.findById(r.getCustomer()).orElseThrow(),productRepository.findById(r.getProduct()).orElseThrow());
        return new ReservationEnt(r.getStartDate(), r.getEndDate(), userRepository.findById(r.getCustomer()).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND)),productRepository.findById(r.getProduct()).orElseThrow());
    }

    public Reservation mapEntToDomain(ReservationEnt r, boolean useID) {
        if (useID)
            return new Reservation(r.getReservationID(), r.getStartDate(), r.getEndDate(), r.getClient().getId(), r.getProduct().getProductID());
        return new Reservation(r.getStartDate(), r.getEndDate(), r.getClient().getId(), r.getProduct().getProductID());
    }

    public Product mapEntToDomain(ProductEnt p, boolean useID) {
        Product product;
        if ("SKI".equals(p.getType())) {
            product = new Ski(p.getPrice(), mapReservationsEntToDomain(p), ((SkiEnt) p).getWeight(), ((SkiEnt) p).getLength());
        } else {
            product = new SkiBoot(p.getPrice(), mapReservationsEntToDomain(p), ((SkiBootEnt) p).getSize());
        }
        if (useID)
            product.setProductID(p.getProductID());
        return product;
    }

    public ProductEnt mapDomainToEnt(Product p, boolean useID) {
        ProductEnt productEnt;
        if ("SKI".equals(p.getType())) {
            productEnt = new SkiEnt(p.getPrice(),mapReservationsDomainToEnt(p) ,((Ski) p).getWeight(), ((Ski) p).getLength());
        } else {
            productEnt = new SkiBootEnt(p.getPrice(),mapReservationsDomainToEnt(p) , ((SkiBoot) p).getSize());
        }
        if (useID) {
            productEnt.setProductID(p.getProductID());
        }
        return productEnt;
    }

    public ClientEnt mapDomainToEnt(Client user, boolean useID) {
        ClientEnt userEnt = new ClientEnt(user.getId(), mapReservationsDomainToEnt(user), user.isActive());
        if (useID)
            userEnt.setId(user.getId());
        return userEnt;
    }

    public Client mapEntToDomain(ClientEnt u, boolean useID) {
        Client user = new Client(u.getId(), this.mapReservationsEntToDomain(u), u.isActive());
        if (useID)
            user.setId(u.getId());
        return user;
    }

    private List<Reservation> mapReservationsEntToDomain(ProductEnt p) {
        return p.getReservations()
                .stream()
                .map(r -> mapEntToDomain(r, true))
                .toList();
    }

    private List<ReservationEnt> mapReservationsDomainToEnt(Product p) {
        return p.getReservations()
                .stream()
                .map(r -> mapDomainToEnt(r, true))
                .toList();
    }

    private List<ReservationEnt> mapReservationsDomainToEnt(Client u) {
        return u.getReservations()
                .stream()
                .map(r -> mapDomainToEnt(r, true))
                .toList();
    }

    private List<Reservation> mapReservationsEntToDomain(ClientEnt u) {
        return  u.getReservations()
                .stream()
                .map(r -> mapEntToDomain(r, true))
                .toList();
    }
}




