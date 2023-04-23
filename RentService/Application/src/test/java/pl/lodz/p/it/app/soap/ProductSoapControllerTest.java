package pl.lodz.p.it.app.soap;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductSoapControllerTest {

    @LocalServerPort
    private int port;

    @Test
    public void getProductByIdTest() throws IOException {
        HttpResponse httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>ae8c5861-3c46-44d4-a090-115d4926395f</productID>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        String response;
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertTrue(response.contains("<ns2:productID>ae8c5861-3c46-44d4-a090-115d4926395f</ns2:productID>"));
        Assertions.assertTrue(response.contains("<ns2:price>980.125</ns2:price>"));
        Assertions.assertTrue(response.contains("<ns2:type>SKI</ns2:type>"));
        Assertions.assertTrue(response.contains("<ns2:weight>12.0</ns2:weight>"));
        Assertions.assertTrue(response.contains("<ns2:length>12.0</ns2:length>"));
        Assertions.assertTrue(response.contains("<ns2:reservation>" +
                "<ns2:reservationID>a134bee4-cf8a-4322-a017-3f05194d066b</ns2:reservationID>" +
                "<ns2:startDate>2023-03-09</ns2:startDate><ns2:endDate>2023-10-17</ns2:endDate>" +
                "<ns2:customer>59e2472c-1137-4646-9928-fc21d4790d40</ns2:customer>" +
                "<ns2:product>ae8c5861-3c46-44d4-a090-115d4926395f</ns2:product>" +
                "</ns2:reservation>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>b86866d7-6210-4d69-afa4-b564594c711a</productID>"));
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertTrue(response.contains("<ns2:productID>b86866d7-6210-4d69-afa4-b564594c711a</ns2:productID>"));
        Assertions.assertTrue(response.contains("<ns2:price>80.99</ns2:price>"));
        Assertions.assertTrue(response.contains("<ns2:type>SKIBOOT</ns2:type>"));
        Assertions.assertTrue(response.contains("<ns2:size>45.0</ns2:size>"));
        Assertions.assertFalse(response.contains("<ns2:weight>"));
        Assertions.assertFalse(response.contains("<ns2:length>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>d8e40cda-633f-4de1-a798-cb7e4094ade7</productID>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("<faultstring xml:lang=\"en\">404 NOT_FOUND</faultstring>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>wrong uuid</productID>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("<faultstring xml:lang=\"en\">409 CONFLICT</faultstring>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "wrong request"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("<faultstring xml:lang=\"en\">409 CONFLICT</faultstring>"));
    }

    @Test
    public void getAllProductsTest() throws IOException {
        HttpResponse httpResponse = this.makeSoapRequest(createRequestString(
                "getAllProductsRequest", ""));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        String response;
        response = new BasicResponseHandler().handleResponse(httpResponse);
        int matches = StringUtils.countMatches(response, "<ns2:product xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        Assertions.assertTrue(matches == 6 || matches == 7);
    }


    @Test
    public void getReservationsForProduct() throws IOException {
        HttpResponse httpResponse = this.makeSoapRequest(createRequestString(
                "getReservationsRequest", "<id>ae8c5861-3c46-44d4-a090-115d4926395f</id><past>true</past>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        String response;
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(StringUtils.countMatches(response, "<ns2:reservationsResponse xmlns:ns2=\"http://pl.lodz.p.it/tks/product\">"), 1);
        Assertions.assertTrue(response.contains("<ns2:reservation>" +
                "<ns2:reservationID>5185cda6-617d-4f1c-a43a-bed4c96a0232</ns2:reservationID>" +
                "<ns2:startDate>2023-02-12</ns2:startDate>" +
                "<ns2:endDate>2023-02-20</ns2:endDate>" +
                "<ns2:customer>59e2472c-1137-4646-9928-fc21d4790d40</ns2:customer>" +
                "<ns2:product>ae8c5861-3c46-44d4-a090-115d4926395f</ns2:product>" +
                "</ns2:reservation>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "getReservationsRequest", "<id>ae8c5861-3c46-44d4-a090-115d4926395f</id>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(StringUtils.countMatches(response, "<ns2:reservationsResponse xmlns:ns2=\"http://pl.lodz.p.it/tks/product\">"), 1);
        Assertions.assertTrue(response.contains("<ns2:reservation>" +
                "<ns2:reservationID>a134bee4-cf8a-4322-a017-3f05194d066b</ns2:reservationID>" +
                "<ns2:startDate>2023-03-09</ns2:startDate>" +
                "<ns2:endDate>2023-10-17</ns2:endDate>" +
                "<ns2:customer>59e2472c-1137-4646-9928-fc21d4790d40</ns2:customer>" +
                "<ns2:product>ae8c5861-3c46-44d4-a090-115d4926395f</ns2:product>" +
                "</ns2:reservation>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "getReservationsRequest", "<id>c12c11ba-664c-411c-96c9-05085bc889d8</id>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertTrue(response.contains("<faultstring xml:lang=\"en\">404 NOT_FOUND \"404 NOT_FOUND\"</faultstring>"));
    }

    @Test
    public void createSkiTest() throws IOException {
        HttpResponse httpResponse = this.makeSoapRequest(createRequestString(
                "createSkiRequest",
                "<ski>" +
                        "<price>123</price>" +
                        "<type>SKI</type>" +
                        "<weight>12</weight>" +
                        "<length>12</length>" +
                        "</ski>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        String response;
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertTrue(response.contains(
                        "<ns2:price>123.0</ns2:price>" +
                        "<ns2:type>SKI</ns2:type>" +
                        "<ns2:weight>12.0</ns2:weight>" +
                        "<ns2:length>12.0</ns2:length>"));
        int pFrom = response.indexOf("<ns2:productID>") + "<ns2:productID>".length();
        int pTo = response.lastIndexOf("</ns2:productID>");
        String id = response.substring(pFrom, pTo);
        httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>" + id + "</productID>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertTrue(response.contains(
                        "<ns2:price>123.0</ns2:price>" +
                        "<ns2:type>SKI</ns2:type>" +
                        "<ns2:weight>12.0</ns2:weight>" +
                        "<ns2:length>12.0</ns2:length>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "createSkiRequest",
                "<ski>" +
                        "<type>SKI</type>" +
                        "<weight>12</weight>" +
                        "<length>12</length>" +
                        "</ski>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertTrue(response.contains("409 CONFLICT"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "createSkiRequest",
                "<ski>" +
                        "<price>24</price>" +
                        "<type>SKIBOOT</type>" +
                        "<weight>12</weight>" +
                        "<length>12</length>" +
                        "</ski>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertTrue(response.contains("409 CONFLICT"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "createSkiRequest",
                        "<price>123</price>" +
                        "<type>SKI</type>" +
                        "<weight>12</weight>" +
                        "<length>12</length>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertTrue(response.contains("409 CONFLICT"));
    }

    @Test
    public void createSkiBootTest() throws IOException {
        HttpResponse httpResponse = this.makeSoapRequest(createRequestString(
                "createSkiBootRequest",
                "<skiBoot>" +
                        "<price>1234</price>" +
                        "<type>SKIBOOT</type>" +
                        "<size>256</size>" +
                        "</skiBoot>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        String response;
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertTrue(response.contains(
                        "<ns2:price>1234.0</ns2:price>" +
                        "<ns2:type>SKIBOOT</ns2:type>" +
                        "<ns2:size>256.0</ns2:size>"));
        int pFrom = response.indexOf("<ns2:productID>") + "<ns2:productID>".length();
        int pTo = response.lastIndexOf("</ns2:productID>");
        String id = response.substring(pFrom, pTo);
        httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>" + id + "</productID>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertTrue(response.contains(
                "<ns2:price>1234.0</ns2:price>" +
                        "<ns2:type>SKIBOOT</ns2:type>" +
                        "<ns2:size>256.0</ns2:size>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "createSkiBootRequest",
                "<ski>" +
                        "<type>SKI</type>" +
                        "<weight>12</weight>" +
                        "<length>12</length>" +
                        "</ski>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertTrue(response.contains("409 CONFLICT"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "createSkiBootRequest",
                "<price>123</price>" +
                        "<type>SKIBOOT</type>" +
                        "<size>12</size>"));
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertTrue(response.contains("409 CONFLICT"));
    }

    @Test
    public void updateSkiTest() throws IOException {

        HttpResponse httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>c10217b3-b723-4212-b978-c222a8b4f29b</productID>"));
        String response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        Assertions.assertTrue(response.contains("<ns2:productID>c10217b3-b723-4212-b978-c222a8b4f29b</ns2:productID>" +
                "<ns2:price>12.75</ns2:price>" +
                "<ns2:type>SKI</ns2:type>" +
                "<ns2:weight>20.0</ns2:weight>" +
                "<ns2:length>173.0</ns2:length>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "updateSkiRequest",
                "<ski>" +
                        "<productID>c10217b3-b723-4212-b978-c222a8b4f29b</productID>" +
                        "<price>746</price>" +
                        "<weight>100</weight>" +
                        "<length>100</length>" +
                        "</ski>"));
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        Assertions.assertTrue(response.contains("<ns2:productID>c10217b3-b723-4212-b978-c222a8b4f29b</ns2:productID>" +
                "<ns2:price>746.0</ns2:price>" +
                "<ns2:type>SKI</ns2:type>" +
                "<ns2:weight>100.0</ns2:weight>" +
                "<ns2:length>100.0</ns2:length>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>c10217b3-b723-4212-b978-c222a8b4f29b</productID>"));
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        Assertions.assertTrue(response.contains("<ns2:productID>c10217b3-b723-4212-b978-c222a8b4f29b</ns2:productID>" +
                "<ns2:price>746.0</ns2:price>" +
                "<ns2:type>SKI</ns2:type>" +
                "<ns2:weight>100.0</ns2:weight>" +
                "<ns2:length>100.0</ns2:length>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "updateSkiRequest",
                "<ski>" +
                        "<productID>6852c633-b0e4-4500-8488-eea41ca9ad93</productID>" +
                        "<price>746</price>" +
                        "<weight>100</weight>" +
                        "<length>100</length>" +
                        "</ski>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("404 NOT_FOUND"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "updateSkiRequest",
                "<ski>" +
                        "<productID>c10217b3-b723-4212-b978-c222a8b4f29b</productID>" +
                        "<price>0</price>" +
                        "<weight>100</weight>" +
                        "<length>100</length>" +
                        "</ski>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("406 NOT_ACCEPTABLE"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "updateSkiRequest",
                "<ski>" +
                        "<productID>c10217b3-b723-4212-b978-c222a8b4f29b</productID>" +
                        "<price>100</price>" +
                        "<weight>-10</weight>" +
                        "<length>100</length>" +
                        "</ski>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("406 NOT_ACCEPTABLE"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "updateSkiRequest",
                ""));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("409 CONFLICT"));
    }


    @Test
    public void updateSkiBootTest() throws IOException {
        HttpResponse httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>255a4740-b310-47d0-9a23-7ba07f338590</productID>"));
        String response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        Assertions.assertTrue(response.contains(
                "<ns2:productID>255a4740-b310-47d0-9a23-7ba07f338590</ns2:productID>" +
                "<ns2:price>99.99</ns2:price>" +
                "<ns2:type>SKIBOOT</ns2:type>" +
                "<ns2:size>37.0</ns2:size>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "updateSkiBootRequest",
                         "<skiBoot>" +
                "<productID>255a4740-b310-47d0-9a23-7ba07f338590</productID>" +
                         "<price>1000.0</price>" +
                         "<size>100.0</size>" +
                          "</skiBoot>"));
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        Assertions.assertTrue(response.contains(
                "<ns2:productID>255a4740-b310-47d0-9a23-7ba07f338590</ns2:productID>" +
                "<ns2:price>1000.0</ns2:price>" +
                "<ns2:type>SKIBOOT</ns2:type>" +
                "<ns2:size>100.0</ns2:size>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>255a4740-b310-47d0-9a23-7ba07f338590</productID>"));
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        Assertions.assertTrue(response.contains("<ns2:productID>255a4740-b310-47d0-9a23-7ba07f338590</ns2:productID>" +
                "<ns2:price>1000.0</ns2:price>" +
                "<ns2:type>SKIBOOT</ns2:type>" +
                "<ns2:size>100.0</ns2:size>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "updateSkiBootRequest",
                "<skiBoot>" +
                        "<productID>6852c633-b0e4-4500-8488-eea41ca9ad93</productID>" +
                        "<price>1000.0</price>" +
                        "<size>100.0</size>" +
                        "</skiBoot>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("404 NOT_FOUND"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "updateSkiBootRequest",
                "<skiBoot>" +
                        "<productID>255a4740-b310-47d0-9a23-7ba07f338590</productID>" +
                        "<price>0</price>" +
                        "<size>100.0</size>" +
                        "</skiBoot>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("406 NOT_ACCEPTABLE"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "updateSkiBootRequest",
                "<skiBoot>" +
                        "<productID>255a4740-b310-47d0-9a23-7ba07f338590</productID>" +
                        "<price>1000.0</price>" +
                        "<size>-10</size>" +
                        "</skiBoot>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("406 NOT_ACCEPTABLE"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "updateSkiBootRequest",
                ""));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("409 CONFLICT"));
    }

    @AfterAll
    public void deleteProductTest() throws IOException {
        HttpResponse httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>ae8c5861-3c46-44d4-a090-115d4926395f</productID>"));
        String response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        Assertions.assertEquals(StringUtils.countMatches(response, "<ns2:reservation>"), 2);
        Assertions.assertTrue(response.contains(
                "<ns2:startDate>2023-03-09</ns2:startDate>" +
                "<ns2:endDate>2023-10-17</ns2:endDate>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "deleteProductRequest", "<productID>ae8c5861-3c46-44d4-a090-115d4926395f</productID>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("406 NOT_ACCEPTABLE"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>c10217b3-b723-4212-b978-c222a8b4f29b</productID>"));
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        Assertions.assertTrue(response.contains(
                "<ns2:productID>c10217b3-b723-4212-b978-c222a8b4f29b</ns2:productID>" +
                        "<ns2:price>746.0</ns2:price>" +
                        "<ns2:type>SKI</ns2:type>" +
                        "<ns2:weight>100.0</ns2:weight>" +
                        "<ns2:length>100.0</ns2:length>"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "deleteProductRequest", "<productID>c10217b3-b723-4212-b978-c222a8b4f29b</productID>"));
        response = new BasicResponseHandler().handleResponse(httpResponse);
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 202);

        httpResponse = this.makeSoapRequest(createRequestString(
                "getProductRequest", "<productID>c10217b3-b723-4212-b978-c222a8b4f29b</productID>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("404 NOT_FOUND"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "deleteProductRequest", "<productID>c10217b3-b723-4212-b978-c222a8b4f29b</productID>"));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("404 NOT_FOUND"));

        httpResponse = this.makeSoapRequest(createRequestString(
                "deleteProductRequest", ""));
        response = new BasicResponseHandler().handleEntity(httpResponse.getEntity());
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), 500);
        Assertions.assertTrue(response.contains("409 CONFLICT"));
    }
    private String createRequestString(String requestName, String arguments) {
        return
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soap:Body>\n" +
                "    <" + requestName + " xmlns=\"http://pl.lodz.p.it/tks/product\">\n" +
                           arguments +
                "    </" + requestName + ">" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";
    }

    private HttpResponse makeSoapRequest(String requestString) {
        try (CloseableHttpClient httpClient =  HttpClients.createDefault()) {
            HttpUriRequest request = RequestBuilder.post()
                    .setUri("http://localhost:" + port + "/ws")
                    .setHeader(HttpHeaders.CONTENT_TYPE, "text/xml")
                    .setEntity(new StringEntity(requestString))
                    .build();
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
