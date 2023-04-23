package pl.lodz.p.it.soap.mappers;

import it.p.lodz.pl.tks.product.ProductSoap;
import it.p.lodz.pl.tks.product.ReservationSoap;
import it.p.lodz.pl.tks.product.SkiBootSoap;
import it.p.lodz.pl.tks.product.SkiSoap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.domain.model.products.Product;
import pl.lodz.p.it.domain.model.products.Ski;
import pl.lodz.p.it.domain.model.products.SkiBoot;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class Mapper {

    public ProductSoap mapProductToProductSoap(Product product, boolean id) {
        if (Objects.equals(product.getType(), "SKI")) {
            SkiSoap skiSoap = new SkiSoap();
            skiSoap.setLength(((Ski) product).getLength());
            skiSoap.setWeight(((Ski) product).getWeight());
            skiSoap.setPrice(product.getPrice());
            if (id) {
                skiSoap.setType(product.getType());
                this.mapReservationListToReservationSoapList(skiSoap, product);
                skiSoap.setProductID(product.getProductID().toString());
            }
            return skiSoap;
        } else {
            SkiBootSoap skiBootSoap = new SkiBootSoap();
            skiBootSoap.setSize(((SkiBoot) product).getSize());
            skiBootSoap.setPrice(product.getPrice());
            if (id) {
                skiBootSoap.setType(product.getType());
                this.mapReservationListToReservationSoapList(skiBootSoap, product);
                skiBootSoap.setProductID(product.getProductID().toString());
            }
            return skiBootSoap;
        }
    }

    public Product mapProductSoapToProduct(ProductSoap productSoap, boolean id) {
        if (Objects.equals(productSoap.getType(), "SKI")) {
            Ski ski = new Ski(productSoap.getPrice(), ((SkiSoap) productSoap).getLength(),
                    ((SkiSoap) productSoap).getWeight());
            if (id) {
                this.mapReservationSoapListToReservationList(productSoap, ski);
                ski.setProductID(UUID.fromString(productSoap.getProductID()));
                ski.setType(productSoap.getType());
            }
            return ski;
        } else if (Objects.equals(productSoap.getType(), "SKIBOOT")) {
            SkiBoot skiBoot = new SkiBoot(productSoap.getPrice(), ((SkiBootSoap) productSoap).getSize());
            if (id) {
                this.mapReservationSoapListToReservationList(productSoap, skiBoot);
                skiBoot.setProductID(UUID.fromString(productSoap.getProductID()));
                skiBoot.setType(productSoap.getType());
            }
            return skiBoot;
        } else {
            System.out.println(productSoap.getType());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public ReservationSoap mapReservationToReservationSoap(Reservation reservation) {
        ReservationSoap reservationSoap = new ReservationSoap();
        reservationSoap.setReservationID(reservation.getReservationID().toString());
        try {
            reservationSoap.setStartDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(reservation.getStartDate().toString()));
            reservationSoap.setEndDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(reservation.getEndDate().toString()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Date conversion failed!");
        }
        reservationSoap.setCustomer(reservation.getCustomer().toString());
        reservationSoap.setProduct(reservation.getProduct().toString());
        return reservationSoap;
    }

    public Reservation mapReservationSoapToReservation(ReservationSoap reservationSoap) {
        Reservation reservation = new Reservation();
        reservation.setReservationID(UUID.fromString(reservationSoap.getReservationID()));
        try {
            reservation.setStartDate(LocalDate.of(reservationSoap.getStartDate().getYear(), reservationSoap.getStartDate().getMonth(), reservationSoap.getStartDate().getDay()));
            reservation.setEndDate(LocalDate.of(reservationSoap.getEndDate().getYear(), reservationSoap.getEndDate().getMonth(), reservationSoap.getEndDate().getDay()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Date conversion failed!");
        }
        reservation.setCustomer(UUID.fromString(reservationSoap.getCustomer()));
        reservation.setProduct(UUID.fromString(reservationSoap.getProduct()));
        return reservation;
    }

    public void mapReservationSoapListToReservationList(ProductSoap productSoap, Product product) {
        product.getReservations().clear();
        for (ReservationSoap reservationSoap : productSoap.getReservation()) {
            product.getReservations().add(mapReservationSoapToReservation(reservationSoap));
        }
    }

    public void mapReservationListToReservationSoapList(ProductSoap productSoap, Product product) {
        productSoap.getReservation().clear();
        for (Reservation reservation : product.getReservations()) {
            productSoap.getReservation().add(mapReservationToReservationSoap(reservation));
        }
    }

    public void mapReservationSoapListToReservationList(List<ReservationSoap> reservationSoapList, List<Reservation> reservationList) {
        reservationList.clear();
        for (ReservationSoap reservationSoap : reservationSoapList) {
            reservationList.add(mapReservationSoapToReservation(reservationSoap));
        }
    }

    public void mapReservationListToReservationSoapList(List<ReservationSoap> reservationSoapList, List<Reservation> reservationList) {
        reservationSoapList.clear();
        for (Reservation reservation : reservationList) {
            reservationSoapList.add(mapReservationToReservationSoap(reservation));
        }
    }



    public List<Product> mapProductSoapListToProductList(List<ProductSoap> productSoapList) {
        return productSoapList.stream().map(r -> mapProductSoapToProduct(r, true)).toList();
    }

    public List<ProductSoap> mapProductListToProductSoapList(List<Product> productList) {
        return productList.stream().map(r -> mapProductToProductSoap(r, true)).toList();
    }
}
