package pl.lodz.p.it.endpoint;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
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
import pl.lodz.p.it.domain.model.CustomerType;
import pl.lodz.p.it.dto.AdministratorDTO;
import pl.lodz.p.it.dto.CustomerDTO;
import pl.lodz.p.it.dto.ModeratorDTO;
import java.io.IOException;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    String administratorJWT;
    String moderatorJWT;
    String customerJWT;

    TestRestTemplate restTemplate = new TestRestTemplate();
    Logger logger = LoggerFactory.getLogger(UserControllerTest.class);
    HttpHeaders headers = new HttpHeaders();

    @BeforeAll
    public void logIn() throws JSONException {
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
    public void getAllCustomersTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + customerJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<CustomerDTO[]> response = restTemplate.exchange(
            createURLWithPort("/customer"),
            HttpMethod.GET, entity, CustomerDTO[].class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(response.getBody().length == 5 || response.getBody().length == 6);
    }

    @Test
    public void getCustomersByNameContainingTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<CustomerDTO[]> response = restTemplate.exchange(
            createURLWithPort("/customer?username={username}"),
            HttpMethod.GET, entity, CustomerDTO[].class, "cus");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 3);
        /// Customer with exact name
        response = restTemplate.exchange(
            createURLWithPort("/customer?username={username}"),
            HttpMethod.GET, entity, CustomerDTO[].class, "customer1");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);

        //customer with non existand username
        response = restTemplate.exchange(
            createURLWithPort("/customer?username={username}"),
            HttpMethod.GET, entity, CustomerDTO[].class, "nonexistent");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 0);

        //customer starting with jo, there is just one
        response = restTemplate.exchange(
            createURLWithPort("/customer?username={username}"),
            HttpMethod.GET, entity, CustomerDTO[].class, "jo");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);
        Assertions.assertEquals(response.getBody()[0].getEmail(), "cj@xyz.pl");
    }

    @Test
    public void getCustomersByExactName() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<CustomerDTO[]> response = restTemplate.exchange(
            createURLWithPort("/customer?exact={exact}"),
            HttpMethod.GET, entity, CustomerDTO[].class, "jon");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);
        Assertions.assertEquals(response.getBody()[0].getEmail(), "cj@xyz.pl");

        response = restTemplate.exchange(
            createURLWithPort("/customer?exact={exact}"),
            HttpMethod.GET, entity, CustomerDTO[].class, "jo");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 0);
    }

    @Test
    public void getAllModeratorsTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<ModeratorDTO[]> response = restTemplate.exchange(
            createURLWithPort("/moderator"),
            HttpMethod.GET, entity, ModeratorDTO[].class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(response.getBody().length == 4 || response.getBody().length == 5);
    }

    @Test
    public void getModeratorsByNameContainingTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<ModeratorDTO[]> response = restTemplate.exchange(
            createURLWithPort("/moderator?username={username}"),
            HttpMethod.GET, entity, ModeratorDTO[].class, "moderator");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 3);

        response = restTemplate.exchange(
            createURLWithPort("/moderator?username={username}"),
            HttpMethod.GET, entity, ModeratorDTO[].class, "moderator1");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);


        response = restTemplate.exchange(
            createURLWithPort("/moderator?username={username}"),
            HttpMethod.GET, entity, ModeratorDTO[].class, "nonexistent");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 0);


        response = restTemplate.exchange(
            createURLWithPort("/moderator?username={username}"),
            HttpMethod.GET, entity, ModeratorDTO[].class, "jo");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);
        Assertions.assertEquals(response.getBody()[0].getEmail(), "mj@xyz.pl");
    }

    @Test
    public void getModeratorsByExactName() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<ModeratorDTO[]> response = restTemplate.exchange(
            createURLWithPort("/moderator?exact={exact}"),
            HttpMethod.GET, entity, ModeratorDTO[].class, "jon_mod");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);
        Assertions.assertEquals(response.getBody()[0].getEmail(), "mj@xyz.pl");

        response = restTemplate.exchange(
            createURLWithPort("/moderator?exact={exact}"),
            HttpMethod.GET, entity, ModeratorDTO[].class, "jon");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 0);
    }

    @Test
    public void getAllAdministratorsTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<AdministratorDTO[]> response = restTemplate.exchange(
            createURLWithPort("/administrator"),
            HttpMethod.GET, entity, AdministratorDTO[].class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(response.getBody().length == 4 || response.getBody().length == 5);
    }

    @Test
    public void getAdministratorsByNameContainingTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<AdministratorDTO[]> response = restTemplate.exchange(
            createURLWithPort("/administrator?username={username}"),
            HttpMethod.GET, entity, AdministratorDTO[].class, "adm");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 3);

        response = restTemplate.exchange(
            createURLWithPort("/administrator?username={username}"),
            HttpMethod.GET, entity, AdministratorDTO[].class, "admin1");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);


        response = restTemplate.exchange(
            createURLWithPort("/administrator?username={username}"),
            HttpMethod.GET, entity, AdministratorDTO[].class, "nonexistent");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 0);


        response = restTemplate.exchange(
            createURLWithPort("/administrator?username={username}"),
            HttpMethod.GET, entity, AdministratorDTO[].class, "jo");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);
        Assertions.assertEquals(response.getBody()[0].getUsername(), "jon_snow");
    }

    @Test
    public void getAdministratorsByExactName() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<AdministratorDTO[]> response = restTemplate.exchange(
            createURLWithPort("/administrator?exact={exact}"),
            HttpMethod.GET, entity, AdministratorDTO[].class, "jon_snow");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 1);
        Assertions.assertEquals(response.getBody()[0].getUsername(), "jon_snow");

        response = restTemplate.exchange(
            createURLWithPort("/administrator?exact={exact}"),
            HttpMethod.GET, entity, AdministratorDTO[].class, "jon");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().length, 0);
    }

    @Test
    public void getUserByIdTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<CustomerDTO> responseCustomer = restTemplate.exchange(
            createURLWithPort("/customer/{id}"),
            HttpMethod.GET, entity, CustomerDTO.class, "4c6319c6-2530-403e-ba1c-7d0e8e505e78");
        Assertions.assertEquals(responseCustomer.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseCustomer.getBody().getUsername(), "jon");
        Assertions.assertEquals(responseCustomer.getBody().getType(), CustomerType.CUSTOMER);
        Assertions.assertEquals(responseCustomer.getBody().getEmail(), "cj@xyz.pl");

        responseCustomer = restTemplate.exchange(
            createURLWithPort("/customer/{id}"),
            HttpMethod.GET, entity, CustomerDTO.class, "fa2751c4-2001-453d-9ec5-9e464267b34e");
        Assertions.assertEquals(responseCustomer.getStatusCode(), HttpStatus.NOT_FOUND);

        ResponseEntity<ModeratorDTO> responseModerator = restTemplate.exchange(
            createURLWithPort("/moderator/{id}"),
            HttpMethod.GET, entity, ModeratorDTO.class, "9b49fcbb-cf38-4db2-b279-bcc02ff154fa");
        Assertions.assertEquals(responseModerator.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseModerator.getBody().getUsername(), "moderator1");
        Assertions.assertEquals(responseModerator.getBody().getType(), CustomerType.MODERATOR);

        responseModerator = restTemplate.exchange(
            createURLWithPort("/moderator/{id}"),
            HttpMethod.GET, entity, ModeratorDTO.class, "fa2751c4-2001-453d-9ec5-9e464267b34e");
        Assertions.assertEquals(responseModerator.getStatusCode(), HttpStatus.NOT_FOUND);

        ResponseEntity<AdministratorDTO> responseAdministrator = restTemplate.exchange(
            createURLWithPort("/administrator/{id}"),
            HttpMethod.GET, entity, AdministratorDTO.class, "19d5503b-1eee-4949-9f2d-e8c5f44eb4c9");
        Assertions.assertEquals(responseAdministrator.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseAdministrator.getBody().getUsername(), "admin1");
        Assertions.assertEquals(responseAdministrator.getBody().getType(), CustomerType.ADMINISTRATOR);

        responseAdministrator = restTemplate.exchange(
            createURLWithPort("/administrator/{id}"),
            HttpMethod.GET, entity, AdministratorDTO.class, "fa2751c4-2001-453d-9ec5-9e464267b34e");
        Assertions.assertEquals(responseAdministrator.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void activateAndDeactivateCustomerTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<CustomerDTO> response = restTemplate.exchange(
            createURLWithPort("/customer/{id}"),
            HttpMethod.GET, entity, CustomerDTO.class, "4c6319c6-2530-403e-ba1c-7d0e8e505e78");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().getUsername(), "jon");
        Assertions.assertEquals(response.getBody().getType(), CustomerType.CUSTOMER);
        Assertions.assertEquals(response.getBody().isActive(), true);

        response = restTemplate.exchange(
            createURLWithPort("/customer/{id}/deactivate"),
            HttpMethod.PUT, entity, CustomerDTO.class, "4c6319c6-2530-403e-ba1c-7d0e8e505e78");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().getUsername(), "jon");
        Assertions.assertEquals(response.getBody().getType(), CustomerType.CUSTOMER);
        Assertions.assertEquals(response.getBody().isActive(), false);

        response = restTemplate.exchange(
            createURLWithPort("/customer/{id}/activate"),
            HttpMethod.PUT, entity, CustomerDTO.class, "4c6319c6-2530-403e-ba1c-7d0e8e505e78");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().getUsername(), "jon");
        Assertions.assertEquals(response.getBody().getType(), CustomerType.CUSTOMER);
        Assertions.assertEquals(response.getBody().isActive(), true);
    }

    @Test
    public void activateDeactivateCustomerTestErrors() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<JSONObject> response = restTemplate.exchange(
            createURLWithPort("/customer/{id}/deactivate"),
            HttpMethod.PUT, entity, JSONObject.class, "dd126c76-2728-47c8-bce1-756aba5e7d7d");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        response = restTemplate.exchange(
            createURLWithPort("/customer/{id}/activate"),
            HttpMethod.PUT, entity, JSONObject.class, "dd126c76-2728-47c8-bce1-756aba5e7d7d");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        response = restTemplate.exchange(
            createURLWithPort("/customer/{id}/deactivate"),
            HttpMethod.PUT, entity, JSONObject.class, "wrong uuid");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        response = restTemplate.exchange(
            createURLWithPort("/customer/{id}/activate"),
            HttpMethod.PUT, entity, JSONObject.class, "wrong uuid");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createAdministratorTest() throws JSONException {
        ///////////////////////CREATE TEST\\\\\\\\\\\\\\\\\\\\
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject administrator = new JSONObject();
        administrator.put("username", "testAdministrator");
        administrator.put("password", "password");
        HttpEntity<String> entity = new HttpEntity<>(administrator.toString(), headers);
        ResponseEntity<AdministratorDTO> response = restTemplate.exchange(
            createURLWithPort("/administrator"),
            HttpMethod.PUT, entity, AdministratorDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().getUsername(), "testAdministrator");

        administrator.put("username", "testAdministrator");
        administrator.put("password", "");
        entity = new HttpEntity<>(administrator.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/administrator"),
            HttpMethod.PUT, entity, AdministratorDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        administrator.put("username", "");
        administrator.put("password", "password");
        entity = new HttpEntity<>(administrator.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/administrator"),
            HttpMethod.PUT, entity, AdministratorDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        administrator.put("username", "admin1");
        administrator.put("password", "password");
        entity = new HttpEntity<>(administrator.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/administrator"),
            HttpMethod.PUT, entity, AdministratorDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entityGet = new HttpEntity<>(null, headers);
        ResponseEntity<AdministratorDTO[]> responseGet = restTemplate.exchange(
            createURLWithPort("/administrator?exact={exact}"),
            HttpMethod.GET, entityGet, AdministratorDTO[].class, "testAdministrator");
        Assertions.assertEquals(responseGet.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseGet.getBody().length, 1);
        Assertions.assertEquals(responseGet.getBody()[0].getUsername(), "testAdministrator");
    }

    @Test
    public void createModeratorTest() throws JSONException {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject moderator = new JSONObject();
        moderator.put("username", "testModerator");
        moderator.put("password", "password");
        moderator.put("email", "xyz.xyz");
        HttpEntity<String> entity = new HttpEntity<>(moderator.toString(), headers);
        ResponseEntity<ModeratorDTO> response = restTemplate.exchange(
            createURLWithPort("/moderator"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().getUsername(), "testModerator");
        Assertions.assertEquals(response.getBody().getEmail(), "xyz.xyz");

        moderator.put("username", "testModerator");
        moderator.put("password", "");
        moderator.put("email", "xyz.xyz.xyz");
        entity = new HttpEntity<>(moderator.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/moderator"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        moderator.put("username", "");
        moderator.put("password", "password");
        moderator.put("email", "xyz.xyz.xyz");
        entity = new HttpEntity<>(moderator.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/moderator"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        moderator.put("username", "testModerator");
        moderator.put("password", "password");
        moderator.put("email", "");
        entity = new HttpEntity<>(moderator.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/moderator"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        moderator.put("username", "moderator1");
        moderator.put("password", "password");
        moderator.put("email", "xyz.xyz.xyz");
        entity = new HttpEntity<>(moderator.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/moderator"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        moderator.put("username", "er");
        moderator.put("password", "password");
        moderator.put("email", "mod1@app.xyz");
        entity = new HttpEntity<>(moderator.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/customer"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);

        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entityGet = new HttpEntity<>(null, headers);
        ResponseEntity<ModeratorDTO[]> responseGet = restTemplate.exchange(
            createURLWithPort("/moderator?exact={exact}"),
            HttpMethod.GET, entityGet, ModeratorDTO[].class, "testModerator");
        Assertions.assertEquals(responseGet.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseGet.getBody().length, 1);
        Assertions.assertEquals(responseGet.getBody()[0].getUsername(), "testModerator");
    }

    @Test
    public void createCustomerTest() throws JSONException {
        headers.clear();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject customer = new JSONObject();
        customer.put("username", "testCustomer");
        customer.put("password", "password");
        customer.put("email", "xyz.xyz.xyz");
        HttpEntity<String> entity = new HttpEntity<>(customer.toString(), headers);
        ResponseEntity<CustomerDTO> response = restTemplate.exchange(
            createURLWithPort("/customer"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody().getUsername(), "testCustomer");
        Assertions.assertEquals(response.getBody().getEmail(), "xyz.xyz.xyz");

        customer.put("username", "testCustomer");
        customer.put("password", "");
        customer.put("email", "xyz.xyz.xyz");
        entity = new HttpEntity<>(customer.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/customer"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        customer.put("username", "");
        customer.put("password", "password");
        customer.put("email", "xyz.xyz.xyz");
        entity = new HttpEntity<>(customer.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/customer"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        customer.put("username", "testCustomer");
        customer.put("password", "password");
        customer.put("email", "");
        entity = new HttpEntity<>(customer.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/customer"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        customer.put("username", "customer1");
        customer.put("password", "password");
        customer.put("email", "xyz.xyz.xyz");
        entity = new HttpEntity<>(customer.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/customer"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        customer.put("username", "m");
        customer.put("password", "password");
        customer.put("email", "cj@xyz.pl");
        entity = new HttpEntity<>(customer.toString(), headers);
        response = restTemplate.exchange(
            createURLWithPort("/customer"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entityGet = new HttpEntity<>(null, headers);
        ResponseEntity<CustomerDTO[]> responseGet = restTemplate.exchange(
            createURLWithPort("/customer?exact={exact}"),
            HttpMethod.GET, entityGet, CustomerDTO[].class, "testCustomer");
        Assertions.assertEquals(responseGet.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseGet.getBody().length, 1);
        Assertions.assertEquals(responseGet.getBody()[0].getUsername(), "testCustomer");
    }

    @Test
    public void deleteCustomerErrorTest() {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<JSONObject> response = restTemplate.exchange(createURLWithPort("/user/{id}"),
            HttpMethod.DELETE, entity, JSONObject.class, "3bc5a915-139f-446b-a10c-ae7451ee380e");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        response = restTemplate.exchange(createURLWithPort("/user/{id}"),
            HttpMethod.DELETE, entity, JSONObject.class, "wrong uuid");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateCustomerTest() throws JSONException {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity;
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "02171fb3-d462-4bd7-9bd0-4ca635e5c9fb");
        jsonObject.put("email", "newEmail");
        jsonObject.put("username", "newUsername");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        ResponseEntity<CustomerDTO> update = restTemplate.exchange(
            createURLWithPort("/customer/update"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(update.getBody().getUsername(), "newUsername");
        Assertions.assertEquals(update.getBody().getEmail(), "newEmail");

        jsonObject.put("username", "customer3");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        update = restTemplate.exchange(
            createURLWithPort("/customer/update"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.CONFLICT);
        jsonObject.put("email", "mj@xyz.pl");
        jsonObject.put("username", "newUsername");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        update = restTemplate.exchange(
            createURLWithPort("/customer/update"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.CONFLICT);

        jsonObject.put("email", "");
        jsonObject.put("username", "newUsername");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        update = restTemplate.exchange(
            createURLWithPort("/customer/update"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        entity = new HttpEntity<>(new JSONObject().toString(), headers);
        update = restTemplate.exchange(
            createURLWithPort("/customer/update"),
            HttpMethod.PUT, entity, CustomerDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.CONFLICT);
    }

    @Test
    public void updateModeratorTest() throws JSONException {
        String jws;
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<JSONObject> responseModerator = restTemplate.exchange(
            createURLWithPort("/moderator/{id}"),
            HttpMethod.GET, entity, JSONObject.class, "45288028-47ac-451b-9881-e09916380076");
        jws = responseModerator.getHeaders().getETag();

        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("moderatorID", "45288028-47ac-451b-9881-e09916380076");
        jsonObject.put("email", "mail");
        jsonObject.put("username", "moderatorName");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        ResponseEntity<ModeratorDTO> update = restTemplate.exchange(
            createURLWithPort("/moderator/update"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.BAD_REQUEST);

        headers.set("If-Match", jws);
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        update = restTemplate.exchange(
            createURLWithPort("/moderator/update"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(update.getBody().getUsername(), "moderatorName");
        Assertions.assertEquals(update.getBody().getEmail(), "mail");

        jsonObject.put("username", "customer3");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        update = restTemplate.exchange(
            createURLWithPort("/moderator/update"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.CONFLICT);
        jsonObject.put("email", "mj@xyz.pl");
        jsonObject.put("username", "sername");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        update = restTemplate.exchange(
            createURLWithPort("/moderator/update"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.CONFLICT);

        jsonObject.put("email", "");
        jsonObject.put("username", "naaame");
        entity = new HttpEntity<>(jsonObject.toString(), headers);
        update = restTemplate.exchange(
            createURLWithPort("/moderator/update"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        entity = new HttpEntity<>(new JSONObject().toString(), headers);
        update = restTemplate.exchange(
            createURLWithPort("/moderator/update"),
            HttpMethod.PUT, entity, ModeratorDTO.class);
        Assertions.assertEquals(update.getStatusCode(), HttpStatus.CONFLICT);
    }

    @Test
    public void userPasswordChangeTest() throws JSONException {
        headers.clear();
        headers.set("Authorization", "Bearer " + administratorJWT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject passwordChange = new JSONObject();
        passwordChange.put("oldPassword", "password");
        passwordChange.put("newPassword", "newPassword");
        HttpEntity<String> entity = new HttpEntity<>(passwordChange.toString(), headers);
        ResponseEntity<JSONObject> response = restTemplate.exchange(createURLWithPort("/passwordChange"),
            HttpMethod.PUT, entity, JSONObject.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

        JSONObject credentials = new JSONObject();
        credentials.put("username", "admin1");
        credentials.put("password", "newPassword");
        JSONObject jsonObject = new JSONObject(postLoginRequest(credentials));
        Assertions.assertTrue(jsonObject.has("jwt"));

        response = restTemplate.exchange(createURLWithPort("/passwordChange"),
            HttpMethod.PUT, entity, JSONObject.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    public String postLoginRequest(JSONObject jsonObject) {
        String responseString;
        try (CloseableHttpClient httpClient =  HttpClients.createDefault()) {
            HttpUriRequest request = RequestBuilder.post()
                .setUri(createURLWithPort("/login"))
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
