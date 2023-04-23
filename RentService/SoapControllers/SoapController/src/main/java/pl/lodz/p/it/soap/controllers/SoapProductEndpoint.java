package pl.lodz.p.it.soap.controllers;

import it.p.lodz.pl.tks.product.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.lodz.p.it.domain.exceptions.WrongParametersException;
import pl.lodz.p.it.domain.model.Reservation;
import pl.lodz.p.it.domain.model.products.Product;
import pl.lodz.p.it.domain.model.products.Ski;
import pl.lodz.p.it.domain.model.products.SkiBoot;
import pl.lodz.p.it.ports.view.ProductServicePort;
import pl.lodz.p.it.soap.mappers.Mapper;
import java.util.List;
import java.util.UUID;

@Endpoint
public class SoapProductEndpoint {
    private static final String NAMESPACE_URI = "http://pl.lodz.p.it/tks/product";

    @Autowired
    private ProductServicePort productService;

    @Autowired
    private Mapper mapper;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getProductRequest")
    @ResponsePayload
    public ProductResponse getProduct(@RequestPayload GetProductRequest request) {
        try {
            Product product = productService.get(UUID.fromString(request.getProductID()));
            ProductSoap productSoap = mapper.mapProductToProductSoap(product, true);
            ProductResponse getProductResponse = new ProductResponse();
            getProductResponse.setProduct(productSoap);
            return getProductResponse;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllProductsRequest")
    @ResponsePayload
    public AllProductsResponse getAllProducts() {
        AllProductsResponse getAllProductsResponse = new AllProductsResponse();
        for (ProductSoap productSoap : mapper.mapProductListToProductSoapList(productService.getAll())) {
            getAllProductsResponse.getProduct().add(productSoap);
        }
        return getAllProductsResponse;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getReservationsRequest")
    @ResponsePayload
    public ReservationsResponse getReservations(@RequestPayload GetReservationsRequest getReservationsRequest) {
        try{
            Product product = productService.get(UUID.fromString(getReservationsRequest.getId()));
            List<Reservation> res;
            if (getReservationsRequest.isPast()) {
                res = productService.getPastReservation(product.getProductID());
            } else {
                res = productService.getFutureReservations(product.getProductID());
            }
            ReservationsResponse getReservationsResponse = new ReservationsResponse();
            mapper.mapReservationListToReservationSoapList(getReservationsResponse.getReservation(), res);
            return getReservationsResponse;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createSkiRequest")
    @ResponsePayload
    public ProductResponse createSki(@RequestPayload CreateSkiRequest skiRequest) {
        ProductResponse productResponse = new ProductResponse();
        try {
            productResponse.setProduct(
                    mapper.mapProductToProductSoap(
                            productService.createSki(
                                    (Ski) mapper.mapProductSoapToProduct(
                                            skiRequest.getSki(), false)), true));
        return productResponse;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createSkiBootRequest")
    @ResponsePayload
    public ProductResponse createSkiBoot(@RequestPayload CreateSkiBootRequest skiBootRequest) {
        try {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setProduct(
                    mapper.mapProductToProductSoap(
                            productService.createSkiBoot(
                                    (SkiBoot) mapper.mapProductSoapToProduct(
                                            skiBootRequest.getSkiBoot(), false)), true));
            return productResponse;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteProductRequest")
    public void delete(@RequestPayload DeleteProductRequest idRequest) {
        try {
            productService.delete(UUID.fromString(idRequest.getProductID()));
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateSkiRequest")
    @ResponsePayload
    public ProductResponse updateSki(@RequestPayload UpdateSkiRequest idRequest) {
        try {
            SkiSoap skiSoap = idRequest.getSki();
            skiSoap.setType("SKI");
            Ski s = productService.modify((Ski) mapper.mapProductSoapToProduct(skiSoap, true));
            ProductResponse productResponse = new ProductResponse();
            productResponse.setProduct(mapper.mapProductToProductSoap(s, true));
            return productResponse;
        } catch (WrongParametersException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateSkiBootRequest")
    @ResponsePayload
    public ProductResponse updateSkiBoot(@RequestPayload UpdateSkiBootRequest idRequest) {
        try {
            SkiBootSoap skiBootSoap = idRequest.getSkiBoot();
            skiBootSoap.setType("SKIBOOT");
            SkiBoot s = productService.modify((SkiBoot) mapper.mapProductSoapToProduct(skiBootSoap, true));
            ProductResponse productResponse = new ProductResponse();
            productResponse.setProduct(mapper.mapProductToProductSoap(s, true));
            return productResponse;
        } catch (WrongParametersException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}
