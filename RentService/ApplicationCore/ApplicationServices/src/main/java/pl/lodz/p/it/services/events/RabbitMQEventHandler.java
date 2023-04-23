package pl.lodz.p.it.services.events;

import lombok.RequiredArgsConstructor;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.domain.model.Client;
import pl.lodz.p.it.ports.view.ClientServicePort;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
public class RabbitMQEventHandler {

    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    ClientServicePort clientServicePort;

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "tks-event")
    public void receiveMessage(String message) {
        JSONObject jsonObject = new JSONObject(message);
        String method = jsonObject.getString("method");
        UUID userId = UUID.fromString(jsonObject.getString("customerId"));
        switch (method) {
            case "createCustomer" -> clientServicePort.createClient(new Client(userId));
            case "deactivateCustomer" -> clientServicePort.deactivateClient(userId);
            case "activateCustomer" -> clientServicePort.activateClient(userId);
        }
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
