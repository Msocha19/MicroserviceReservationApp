package pl.lodz.p.it.app.rest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.DeleteMapping;
import pl.lodz.p.it.dto.ReservationDTO;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationControllerTest {

    @LocalServerPort
    private int port;

    String administratorJWT;
    String moderatorJWT;
    String customerJWT;

    TestRestTemplate restTemplate = new TestRestTemplate();
    Logger logger = LoggerFactory.getLogger(ReservationControllerTest.class);
    HttpHeaders headers = new HttpHeaders();

    @BeforeAll
    public void logIn() {
        JSONObject credentials = new JSONObject();
        credentials.put("username", "admin1");
        credentials.put("password", "password");
        JSONObject jsonObject = new JSONObject(postLoginRequest(credentials));
        administratorJWT = jsonObject.getString("jwt");
        credentials.put("username", "customer3");
        jsonObject = new JSONObject(postLoginRequest(credentials));
        customerJWT = jsonObject.getString("jwt");
        credentials.put("username", "moderator1");
        jsonObject = new JSONObject(postLoginRequest(credentials));
        moderatorJWT = jsonObject.getString("jwt");
    }

    @Test
    public void getReservationByIdTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<ReservationDTO> response = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.GET, entity, ReservationDTO.class, "5185cda6-617d-4f1c-a43a-bed4c96a0232");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().endDate, LocalDate.of(2023, 2, 20));
        Assertions.assertEquals(response.getBody().startDate, LocalDate.of(2023, 2, 12));
        Assertions.assertEquals(response.getBody().customer, UUID.fromString("59e2472c-1137-4646-9928-fc21d4790d40"));
        Assertions.assertEquals(response.getBody().product, UUID.fromString("ae8c5861-3c46-44d4-a090-115d4926395f"));

        response = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.GET, entity, ReservationDTO.class, "b3a9ff79-4f35-4f60-800d-d267b28b2175");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        response = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.GET, entity, ReservationDTO.class, "wrong uuid");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void putReservationTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + customerJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startDate",LocalDate.of(2024,3,18));
        jsonObject.put("endDate",LocalDate.of(2025, 3, 24));
        jsonObject.put("customer", "86a3b048-45b4-4de9-8b42-855d4fa8b0c4");
        jsonObject.put("product", "cf1e58ba-fc4f-4251-85cd-f3d7819a3659");
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
        ResponseEntity<ReservationDTO> response = restTemplate.exchange(
            createURLWithPort("/reservation"),
            HttpMethod.PUT, entity, ReservationDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(response.getBody().reservationID);
        String id = response.getBody().reservationID.toString();
        Assertions.assertEquals(response.getBody().startDate, LocalDate.of(2024,3,18));
        Assertions.assertEquals(response.getBody().endDate, LocalDate.of(2025, 3, 24));
        Assertions.assertEquals(response.getBody().customer, UUID.fromString("86a3b048-45b4-4de9-8b42-855d4fa8b0c4"));
        Assertions.assertEquals(response.getBody().product, UUID.fromString("cf1e58ba-fc4f-4251-85cd-f3d7819a3659"));

        headers.set("Authorization", "Bearer " + administratorJWT);
        entity = new HttpEntity<>(null, headers);

        response = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.GET, entity, ReservationDTO.class, id);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().startDate, LocalDate.of(2024,3,18));
        Assertions.assertEquals(response.getBody().endDate, LocalDate.of(2025, 3, 24));
        Assertions.assertEquals(response.getBody().customer, UUID.fromString("86a3b048-45b4-4de9-8b42-855d4fa8b0c4"));
        Assertions.assertEquals(response.getBody().product, UUID.fromString("cf1e58ba-fc4f-4251-85cd-f3d7819a3659"));

        ResponseEntity<ReservationDTO[]> checkIfReservationCreated= restTemplate.exchange(
            createURLWithPort("/product/{id}/reservations"),
            HttpMethod.GET, entity, ReservationDTO[].class, "cf1e58ba-fc4f-4251-85cd-f3d7819a3659");
        Assertions.assertEquals(checkIfReservationCreated.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(checkIfReservationCreated.getBody().length, 1);
        Assertions.assertEquals(checkIfReservationCreated.getBody()[0].startDate, LocalDate.of(2024,3,18));
        Assertions.assertEquals(checkIfReservationCreated.getBody()[0].endDate, LocalDate.of(2025, 3, 24));
        Assertions.assertEquals(checkIfReservationCreated.getBody()[0].customer, UUID.fromString("86a3b048-45b4-4de9-8b42-855d4fa8b0c4"));
        Assertions.assertEquals(checkIfReservationCreated.getBody()[0].product, UUID.fromString("cf1e58ba-fc4f-4251-85cd-f3d7819a3659"));

        checkIfReservationCreated = restTemplate.exchange(
            createURLWithPort("/reservation/client?id={id}"),
            HttpMethod.GET, entity, ReservationDTO[].class, "86a3b048-45b4-4de9-8b42-855d4fa8b0c4");
        Assertions.assertEquals(checkIfReservationCreated.getBody().length, 1);
        Assertions.assertEquals(checkIfReservationCreated.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(checkIfReservationCreated.getBody()[0].startDate, LocalDate.of(2024,3,18));
        Assertions.assertEquals(checkIfReservationCreated.getBody()[0].endDate, LocalDate.of(2025, 3, 24));
        Assertions.assertEquals(checkIfReservationCreated.getBody()[0].customer, UUID.fromString("86a3b048-45b4-4de9-8b42-855d4fa8b0c4"));
        Assertions.assertEquals(checkIfReservationCreated.getBody()[0].product, UUID.fromString("cf1e58ba-fc4f-4251-85cd-f3d7819a3659"));
    }

    @Test
    public void putReservationErrorCaseTest() {
        //already reserved product
        headers.clear();
        headers.set("Authorization", "Bearer " + customerJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startDate",LocalDate.of(2023,4,18));
        jsonObject.put("endDate",LocalDate.of(2025, 3, 24));
        jsonObject.put("customer", "86a3b048-45b4-4de9-8b42-855d4fa8b0c4");
        jsonObject.put("product", "ae8c5861-3c46-44d4-a090-115d4926395f");
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
        ResponseEntity<ReservationDTO> response = restTemplate.exchange(
            createURLWithPort("/reservation"),
            HttpMethod.PUT, entity, ReservationDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
        //product not found
        jsonObject.put("product", "f75a24ae-baeb-4b49-9b58-c919fc4b528b");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/reservation"),
            HttpMethod.PUT, entity, ReservationDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        //customer not found
        jsonObject.put("customer", "f75a24ae-baeb-4b49-9b58-c919fc4b528b");
        jsonObject.put("product", "cf1e58ba-fc4f-4251-85cd-f3d7819a3659");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/reservation"),
            HttpMethod.PUT, entity, ReservationDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        //wrong dates
        jsonObject.put("startDate",LocalDate.of(2022,3,18));
        jsonObject.put("endDate",LocalDate.of(2027, 3, 24));
        jsonObject.put("customer", "86a3b048-45b4-4de9-8b42-855d4fa8b0c4");
        jsonObject.put("product", "cf1e58ba-fc4f-4251-85cd-f3d7819a3659");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/reservation"),
            HttpMethod.PUT, entity, ReservationDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        jsonObject.put("startDate",LocalDate.of(2027,3,18));
        jsonObject.put("endDate",LocalDate.of(2026, 3, 24));
        jsonObject.put("customer", "86a3b048-45b4-4de9-8b42-855d4fa8b0c4");
        jsonObject.put("product", "cf1e58ba-fc4f-4251-85cd-f3d7819a3659");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/reservation"),
            HttpMethod.PUT, entity, ReservationDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
        //inactive client
        jsonObject.put("startDate",LocalDate.of(2026,3,18));
        jsonObject.put("endDate",LocalDate.of(2027, 3, 24));
        jsonObject.put("customer", "02171fb3-d462-4bd7-9bd0-4ca635e5c9fb");
        jsonObject.put("product", "cf1e58ba-fc4f-4251-85cd-f3d7819a3659");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/reservation"),
            HttpMethod.PUT, entity, ReservationDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
    }

    @Test
    public void deleteReservationTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ReservationDTO> res = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.GET, entity, ReservationDTO.class,"b9598e58-1a9f-4bd1-877f-3c7e442aa794");
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);

        ResponseEntity<JSONObject> response = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.DELETE, entity, JSONObject.class, "b9598e58-1a9f-4bd1-877f-3c7e442aa794");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

        res = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.GET, entity, ReservationDTO.class, "b9598e58-1a9f-4bd1-877f-3c7e442aa794");
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);

        ResponseEntity<ReservationDTO[]> reservationDeletionCheck = restTemplate.exchange(
            createURLWithPort("/product/{id}/reservations"),
            HttpMethod.GET, entity, ReservationDTO[].class, "5470ec59-01f6-426c-b7fd-2dd11212bb11");
        Assertions.assertEquals(reservationDeletionCheck.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(reservationDeletionCheck.getBody().length, 1);

        reservationDeletionCheck = restTemplate.exchange(
            createURLWithPort("/reservation/client?id={id}"),
            HttpMethod.GET, entity, ReservationDTO[].class, "59e2472c-1137-4646-9928-fc21d4790d40");
        Assertions.assertEquals(reservationDeletionCheck.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(Objects.requireNonNull(reservationDeletionCheck.getBody()).length, 2);
    }

    @Test
    public void deleteReservationErrorCaseTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        //not found reservation
        ResponseEntity<JSONObject> response = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.DELETE, entity, JSONObject.class, "f2ce4c9b-afcc-439c-89bf-78e9f152303e");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        //reservation is ongoing
        response = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.DELETE, entity, JSONObject.class, "a134bee4-cf8a-4322-a017-3f05194d066b");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
        //reservation has finished
        response = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.DELETE, entity, JSONObject.class, "5185cda6-617d-4f1c-a43a-bed4c96a0232");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
        //wrong uuid
        response = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.DELETE, entity, JSONObject.class, "wrong uuid");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteReservationForcefullyTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ReservationDTO> res = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.GET, entity, ReservationDTO.class,"5185cda6-617d-4f1c-a43a-bed4c96a0232");
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);

        ResponseEntity<JSONObject> response = restTemplate.exchange(
            createURLWithPort("/reservation/forced/{id}"),
            HttpMethod.DELETE, entity, JSONObject.class, "5185cda6-617d-4f1c-a43a-bed4c96a0232");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

        res = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.GET, entity, ReservationDTO.class, "5185cda6-617d-4f1c-a43a-bed4c96a0232");
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);

        ResponseEntity<ReservationDTO[]> reservationDeletionCheck = restTemplate.exchange(
            createURLWithPort("/product/{id}/reservations"),
            HttpMethod.GET, entity, ReservationDTO[].class, "ae8c5861-3c46-44d4-a090-115d4926395f");
        Assertions.assertEquals(reservationDeletionCheck.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(reservationDeletionCheck.getBody().length, 1);

        reservationDeletionCheck = restTemplate.exchange(
            createURLWithPort("/reservation/client?id={id}"),
            HttpMethod.GET, entity, ReservationDTO[].class, "59e2472c-1137-4646-9928-fc21d4790d40");
        Assertions.assertEquals(reservationDeletionCheck.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(reservationDeletionCheck.getBody().length == 2 || reservationDeletionCheck.getBody().length == 3);

        response = restTemplate.exchange(
            createURLWithPort("/reservation/forced/{id}"),
            HttpMethod.DELETE, entity, JSONObject.class, "wrong uuid");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        response = restTemplate.exchange(
            createURLWithPort("/reservation/forced/{id}"),
            HttpMethod.DELETE, entity, JSONObject.class, "f2ce4c9b-afcc-439c-89bf-78e9f152303e");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateReservationTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<ReservationDTO> res = restTemplate.exchange(
            createURLWithPort("/reservation/{id}"),
            HttpMethod.GET, entity, ReservationDTO.class,"89fdca80-8b20-43db-8e74-a7cffebcac9f");
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        String jws = res.getHeaders().getETag();
        headers.set("If-Match", jws);
        System.out.println("JWS: " + jws);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reservationID", UUID.fromString("89fdca80-8b20-43db-8e74-a7cffebcac9f"));
        jsonObject.put("startDate", LocalDate.of(2023,04,12));
        jsonObject.put("endDate", LocalDate.of(2024,05,14));
        jsonObject.put("customer", UUID.fromString("02171fb3-d462-4bd7-9bd0-4ca635e5c9fb"));
        jsonObject.put("product", UUID.fromString("255a4740-b310-47d0-9a23-7ba07f338590"));
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        res = restTemplate.exchange(
            createURLWithPort("/reservation/update"),
            HttpMethod.PUT, entity, ReservationDTO.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(res.getBody().reservationID, UUID.fromString("89fdca80-8b20-43db-8e74-a7cffebcac9f"));
        Assertions.assertEquals(res.getBody().startDate, LocalDate.of(2023,04,12));
        Assertions.assertEquals(res.getBody().endDate, LocalDate.of(2024,05,14));
        Assertions.assertEquals(res.getBody().product, UUID.fromString("255a4740-b310-47d0-9a23-7ba07f338590"));
        Assertions.assertEquals(res.getBody().customer, UUID.fromString("02171fb3-d462-4bd7-9bd0-4ca635e5c9fb"));

        jsonObject.put("reservationID", UUID.fromString("89fdca80-8b20-43db-8e74-a7cffebcac9f"));
        jsonObject.put("startDate", LocalDate.of(2023,04,12));
        jsonObject.put("endDate", LocalDate.of(2024,05,14));
        jsonObject.put("customer", "wrong");
        jsonObject.put("product", UUID.fromString("255a4740-b310-47d0-9a23-7ba07f338590"));
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        res = restTemplate.exchange(
            createURLWithPort("/reservation/update"),
            HttpMethod.PUT, entity, ReservationDTO.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);

        jsonObject.put("reservationID", UUID.fromString("89fdca80-8b20-43db-8e74-a7cffebcac9f"));
        jsonObject.put("startDate", LocalDate.of(2023,04,12));
        jsonObject.put("endDate", LocalDate.of(2024,05,14));
        jsonObject.put("customer", UUID.fromString("2a8398c3-6aa9-4a38-8f11-a2bd73f59256"));
        jsonObject.put("product", UUID.fromString("255a4740-b310-47d0-9a23-7ba07f338590"));
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        res = restTemplate.exchange(
            createURLWithPort("/reservation/update"),
            HttpMethod.PUT, entity, ReservationDTO.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void getReservationForCustomerTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<ReservationDTO[]> res = restTemplate.exchange(
            createURLWithPort("/reservation/client?id={id}"),
            HttpMethod.GET, entity, ReservationDTO[].class,"59e2472c-1137-4646-9928-fc21d4790d40");
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(res.getBody().length != 0);
        Assertions.assertEquals(res.getBody()[0].customer, UUID.fromString("59e2472c-1137-4646-9928-fc21d4790d40"));

        ResponseEntity<String> res1 = restTemplate.exchange(
            createURLWithPort("/reservation/client?id={id}"),
            HttpMethod.GET, entity, String.class,"8c409339-4f5b-41a7-a499-db060be4f6cc");
        Assertions.assertEquals(res1.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void getReservationsForProductTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<ReservationDTO[]> res = restTemplate.exchange(
            createURLWithPort("/reservation/product/{id}"),
            HttpMethod.GET, entity, ReservationDTO[].class,"ae8c5861-3c46-44d4-a090-115d4926395f");
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(res.getBody().length != 0);
        Assertions.assertEquals(res.getBody()[0].product, UUID.fromString("ae8c5861-3c46-44d4-a090-115d4926395f"));

        ResponseEntity<String> res1 = restTemplate.exchange(
            createURLWithPort("/reservation/product/{id}"),
            HttpMethod.GET, entity, String.class,"8c409339-4f5b-41a7-a499-db060be4f6cc");
        Assertions.assertEquals(res1.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    public String postLoginRequest(JSONObject jsonObject) {
        String responseString;
        try (CloseableHttpClient httpClient =  HttpClients.createDefault()) {
            HttpUriRequest request = RequestBuilder.post()
                .setUri("http://localhost:" + 8081 + "/login")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setEntity(new StringEntity(jsonObject.toString()))
                .build();
            HttpResponse response = httpClient.execute(request);
            responseString = new BasicResponseHandler().handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseString;
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
