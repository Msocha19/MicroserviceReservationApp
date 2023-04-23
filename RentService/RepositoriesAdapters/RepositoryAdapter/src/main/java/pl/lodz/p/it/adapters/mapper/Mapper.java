package pl.lodz.p.it.adapters.mapper;

import pl.lodz.p.it.domain.model.Client;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.model.ClientEnt;
import pl.lodz.p.it.model.products.ProductEnt;
import pl.lodz.p.it.model.ReservationEnt;
import pl.lodz.p.it.domain.model.products.Product;

public interface Mapper {
    ReservationEnt mapDomainToEnt(Reservation r, boolean useID);

    Reservation mapEntToDomain(ReservationEnt r, boolean useID);

    Product mapEntToDomain(ProductEnt p, boolean useID);

    ProductEnt mapDomainToEnt(Product p, boolean useID);

    ClientEnt mapDomainToEnt(Client u, boolean useID);

    Client mapEntToDomain(ClientEnt u, boolean useID);
}
