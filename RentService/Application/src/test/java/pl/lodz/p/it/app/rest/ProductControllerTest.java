package pl.lodz.p.it.app.rest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.lodz.p.it.dto.ReservationDTO;
import pl.lodz.p.it.dto.SkiBootDTO;
import pl.lodz.p.it.dto.SkiDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductControllerTest {

    @LocalServerPort
    private int port;

    String administratorJWT;
    String moderatorJWT;
    String customerJWT;

    TestRestTemplate restTemplate = new TestRestTemplate();
    Logger logger = LoggerFactory.getLogger(ProductControllerTest.class);
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
    public void getProductsTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + moderatorJWT);
        JSONArray jsonArray;
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        System.out.println(createURLWithPort(""));
        ResponseEntity<String> response = restTemplate.exchange(
            createURLWithPort(""),
            HttpMethod.GET, entity, String.class);
        jsonArray = new JSONArray(response.getBody());
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(jsonArray.length() == 6 || jsonArray.length() == 7);
    }

    @Test
    public void getProductByProductId() {
        headers.clear();
        headers.set("Authorization", "Bearer " + moderatorJWT);
        JSONObject result;
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "255a4740-b310-47d0-9a23-7ba07f338590");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        result = new JSONObject(response.getBody());
        Assertions.assertEquals(result.get("productID"), "255a4740-b310-47d0-9a23-7ba07f338590");
        Assertions.assertEquals(new JSONArray(result.get("reservations").toString()).length(), 0);
        Assertions.assertEquals(result.get("size").toString(), "37.0");
        Assertions.assertEquals(result.get("reserved"), false);
        Assertions.assertEquals(result.get("price").toString(), "99.99");
        Assertions.assertEquals(result.get("type"), "SKIBOOT");

        response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "5470ec59-01f6-426c-b7fd-2dd11212bb11");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        result = new JSONObject(response.getBody());
        Assertions.assertEquals(result.get("productID"), "5470ec59-01f6-426c-b7fd-2dd11212bb11");
        Assertions.assertEquals(new JSONArray(result.get("reservations").toString()).length(), 2);
        Assertions.assertEquals(result.get("length").toString(), "12.0");
        Assertions.assertEquals(result.get("weight").toString(), "13.0");
        Assertions.assertEquals(result.get("reserved"), false);
        Assertions.assertEquals(result.get("price").toString(), "180.0");
        Assertions.assertEquals(result.get("type"), "SKI");

        response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "5b4b8e2c-2e7f-47e1-9b4f-306f438308e0");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "WRONG UUID");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getReservationsForProduct() {
        headers.clear();
        headers.set("Authorization", "Bearer " + moderatorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<ReservationDTO[]> response = restTemplate.exchange(
            createURLWithPort("/{id}/reservations"),
            HttpMethod.GET, entity, ReservationDTO[].class, "ae8c5861-3c46-44d4-a090-115d4926395f");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);
        Assertions.assertEquals(response.getBody()[0].product, UUID.fromString("ae8c5861-3c46-44d4-a090-115d4926395f"));
        Assertions.assertEquals(response.getBody()[0].customer,  UUID.fromString("59e2472c-1137-4646-9928-fc21d4790d40"));
        Assertions.assertEquals(response.getBody()[0].startDate, LocalDate.of(2023,3,9));
        Assertions.assertEquals(response.getBody()[0].endDate, LocalDate.of(2023,10,17));

        response = restTemplate.exchange(
            createURLWithPort("/{id}/reservations?past={past}"),
            HttpMethod.GET, entity, ReservationDTO[].class, "ae8c5861-3c46-44d4-a090-115d4926395f", "true");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);
        Assertions.assertEquals(response.getBody()[0].product, UUID.fromString("ae8c5861-3c46-44d4-a090-115d4926395f"));
        Assertions.assertEquals(response.getBody()[0].customer, UUID.fromString("59e2472c-1137-4646-9928-fc21d4790d40"));
        Assertions.assertEquals(response.getBody()[0].startDate, LocalDate.of(2023,2,12));
        Assertions.assertEquals(response.getBody()[0].endDate, LocalDate.of(2023,2,20));

        ResponseEntity<JSONObject> res = restTemplate.exchange(
            createURLWithPort("/{id}/reservations?past={past}"),
            HttpMethod.GET, entity, JSONObject.class, "Wrong", "true");
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);

        res = restTemplate.exchange(
            createURLWithPort("/{id}/reservations?past={past}"),
            HttpMethod.GET, entity, JSONObject.class, "cd57698e-3d8e-4115-9a4e-6d2b90649d96", "true");
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void createSkiTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + moderatorJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject ski = new JSONObject();
        ski.put("productID", "ae8c5861-3c46-44d4-a090-115d4926395f");
        ski.put("price", 123.4);
        ski.put("weight", 20);
        ski.put("length", 186);
        HttpEntity<String> entity = new HttpEntity<>(ski.toString(), headers);
        ResponseEntity<SkiDTO> response = restTemplate.exchange(
            createURLWithPort("/ski"),
            HttpMethod.PUT, entity, SkiDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(response.getBody().productID);
        String skiID = response.getBody().productID.toString();
        Assertions.assertNotNull(response.getBody().reservations);
        Assertions.assertEquals(response.getBody().length, 186.0);
        Assertions.assertEquals(response.getBody().weight, 20.0);
        Assertions.assertEquals(response.getBody().price, 123.4);

        JSONObject getProduct;

        ResponseEntity<String> getResponse = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, skiID);
        Assertions.assertEquals(getResponse.getStatusCode(), HttpStatus.OK);
        getProduct = new JSONObject(getResponse.getBody());
        Assertions.assertEquals(getProduct.get("type"), "SKI");
        Assertions.assertEquals(getProduct.get("length").toString(), "186.0");
        Assertions.assertEquals(getProduct.get("weight").toString(), "20.0");
        Assertions.assertEquals(getProduct.get("price").toString(), "123.4");
    }

    @Test
    public void createSkiBootTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + moderatorJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject ski = new JSONObject();
        ski.put("productID", "ae8c5861-3c46-44d4-a090-115d4926395f");
        ski.put("price", 123.4);
        ski.put("size", 20);
        HttpEntity<String> entity = new HttpEntity<>(ski.toString(), headers);
        ResponseEntity<SkiBootDTO> response = restTemplate.exchange(
            createURLWithPort("/skiboot"),
            HttpMethod.PUT, entity, SkiBootDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(response.getBody().productID);
        String skiBootID = response.getBody().productID.toString();
        Assertions.assertNotNull(response.getBody().reservations);
        Assertions.assertEquals(response.getBody().size, 20.0);
        Assertions.assertEquals(response.getBody().price, 123.4);

        JSONObject getProduct;

        ResponseEntity<String> getResponse = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, skiBootID);
        Assertions.assertEquals(getResponse.getStatusCode(), HttpStatus.OK);
        getProduct = new JSONObject(getResponse.getBody());
        Assertions.assertEquals(getProduct.get("type"), "SKIBOOT");
        Assertions.assertEquals(getProduct.get("size").toString(), "20.0");
        Assertions.assertEquals(getProduct.get("price").toString(), "123.4");

    }

    @Test
    public void updateSkiTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + moderatorJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject newSki = new JSONObject();
        newSki.put("productID", "c10217b3-b723-4212-b978-c222a8b4f29b");
        newSki.put("price", 100);
        newSki.put("weight", 100);
        newSki.put("length", 100);
        HttpEntity<String> entity = new HttpEntity<>(newSki.toString(), headers);

        ResponseEntity<String> getResponse = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "c10217b3-b723-4212-b978-c222a8b4f29b");
        Assertions.assertEquals(getResponse.getStatusCode(), HttpStatus.OK);
        JSONObject jsonObject = new JSONObject(getResponse.getBody());
        Assertions.assertEquals(jsonObject.get("type"), "SKI");
        Assertions.assertEquals(jsonObject.get("weight").toString(), "20.0");
        Assertions.assertEquals(jsonObject.get("length").toString(), "173.0");
        Assertions.assertEquals(jsonObject.get("price").toString(), "12.75");

        ResponseEntity<SkiDTO> response = restTemplate.exchange(
            createURLWithPort("/update/ski"),
            HttpMethod.PUT, entity, SkiDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().price, 100);
        Assertions.assertEquals(response.getBody().length, 100);
        Assertions.assertEquals(response.getBody().weight, 100);

        getResponse = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "c10217b3-b723-4212-b978-c222a8b4f29b");
        Assertions.assertEquals(getResponse.getStatusCode(), HttpStatus.OK);
        jsonObject = new JSONObject(getResponse.getBody());
        Assertions.assertEquals(jsonObject.get("type"), "SKI");
        Assertions.assertEquals(jsonObject.get("weight").toString(), "100.0");
        Assertions.assertEquals(jsonObject.get("length").toString(), "100.0");
        Assertions.assertEquals(jsonObject.get("price").toString(), "100.0");


        newSki.put("productID", "6dec5b7a-d3ca-4a56-a291-6235db65e7c8");
        entity = new HttpEntity<>(newSki.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/update/ski"),
            HttpMethod.PUT, entity, SkiDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        newSki.put("productID", "c10217b3-b723-4212-b978-c222a8b4f29b");
        newSki.put("price", 0);
        newSki.put("weight", 100);
        newSki.put("length", 100);
        entity = new HttpEntity<>(newSki.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/update/ski"),
            HttpMethod.PUT, entity, SkiDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        newSki.put("price", 100);
        newSki.put("weight", -10);
        newSki.put("length", 100);
        entity = new HttpEntity<>(newSki.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/update/ski"),
            HttpMethod.PUT, entity, SkiDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(
            createURLWithPort("/update/ski"),
            HttpMethod.PUT, entity, SkiDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateSkiBootTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + moderatorJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject newSkiBoot = new JSONObject();
        newSkiBoot.put("productID", "255a4740-b310-47d0-9a23-7ba07f338590");
        newSkiBoot.put("price", 100);
        newSkiBoot.put("size", 100);
        HttpEntity<String> entity = new HttpEntity<>(newSkiBoot.toString(), headers);

        ResponseEntity<String> getResponse = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "255a4740-b310-47d0-9a23-7ba07f338590");
        Assertions.assertEquals(getResponse.getStatusCode(), HttpStatus.OK);
        JSONObject jsonObject = new JSONObject(getResponse.getBody());
        Assertions.assertEquals(jsonObject.get("type"), "SKIBOOT");
        Assertions.assertEquals(jsonObject.get("size").toString(), "37.0");
        Assertions.assertEquals(jsonObject.get("price").toString(), "99.99");

        ResponseEntity<SkiBootDTO> response = restTemplate.exchange(
            createURLWithPort("/update/skiboot"),
            HttpMethod.PUT, entity, SkiBootDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().price, 100);
        Assertions.assertEquals(response.getBody().size, 100);

        getResponse = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "255a4740-b310-47d0-9a23-7ba07f338590");
        Assertions.assertEquals(getResponse.getStatusCode(), HttpStatus.OK);
        jsonObject = new JSONObject(getResponse.getBody());
        Assertions.assertEquals(jsonObject.get("type"), "SKIBOOT");
        Assertions.assertEquals(jsonObject.get("size").toString(), "100.0");
        Assertions.assertEquals(jsonObject.get("price").toString(), "100.0");


        newSkiBoot.put("productID", "6dec5b7a-d3ca-4a56-a291-6235db65e7c8");
        entity = new HttpEntity<>(newSkiBoot.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/update/skiboot"),
            HttpMethod.PUT, entity, SkiBootDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        newSkiBoot.put("productID", "255a4740-b310-47d0-9a23-7ba07f338590");
        newSkiBoot.put("price", 0);
        newSkiBoot.put("size", 100);
        entity = new HttpEntity<>(newSkiBoot.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/update/ski"),
            HttpMethod.PUT, entity, SkiBootDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        newSkiBoot.put("price", 100);
        newSkiBoot.put("size", -10);
        entity = new HttpEntity<>(newSkiBoot.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/update/skiboot"),
            HttpMethod.PUT, entity, SkiBootDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(
            createURLWithPort("/update/skiboot"),
            HttpMethod.PUT, entity, SkiBootDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @AfterAll
    public void deleteProductTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + moderatorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "5470ec59-01f6-426c-b7fd-2dd11212bb11");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        JSONObject jsonObject = new JSONObject(response.getBody());
        Assertions.assertEquals(jsonObject.get("type"), "SKI");
        Assertions.assertEquals(jsonObject.get("length").toString(), "12.0");
        Assertions.assertEquals(jsonObject.get("weight").toString(), "13.0");
        Assertions.assertEquals(jsonObject.get("price").toString(), "180.0");
        Assertions.assertEquals(new JSONArray(jsonObject.get("reservations").toString()).length(), 2);

        response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.DELETE, entity, String.class, "5470ec59-01f6-426c-b7fd-2dd11212bb11");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "c10217b3-b723-4212-b978-c222a8b4f29b");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        jsonObject = new JSONObject(response.getBody());
        Assertions.assertEquals(jsonObject.get("type"), "SKI");
        Assertions.assertEquals(jsonObject.get("length").toString(), "100.0");
        Assertions.assertEquals(jsonObject.get("weight").toString(), "100.0");
        Assertions.assertEquals(jsonObject.get("price").toString(), "100.0");
        Assertions.assertEquals(new JSONArray(jsonObject.get("reservations").toString()).length(), 0);

        response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.DELETE, entity, String.class, "c10217b3-b723-4212-b978-c222a8b4f29b");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

        response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.GET, entity, String.class, "c10217b3-b723-4212-b978-c222a8b4f29b");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.DELETE, entity, String.class, "c10217b3-b723-4212-b978-c222a8b4f29b");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        response = restTemplate.exchange(
            createURLWithPort("/{id}"),
            HttpMethod.DELETE, entity, String.class, "WRONG UUID");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
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
        return "http://localhost:" + port + "/product" + uri;
    }
}
