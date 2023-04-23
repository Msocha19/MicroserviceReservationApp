package pl.lodz.p.it.services.events;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.ports.view.UserServicePort;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
public class RabbitMQEventHandler {

    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    UserServicePort userServicePort;

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "tks-response")
    public void receiveMessage(String message) {
        JSONObject jsonObject = new JSONObject(message);
        String method = jsonObject.getString("method");
        UUID userId = UUID.fromString(jsonObject.getString("customerId"));
        switch (method) {
            case "activateCustomer" -> userServicePort.changeActiveCustomer(userId, true, true);
            case "deactivateCustomer" -> userServicePort.changeActiveCustomer(userId, false, true);
            case "deleteCustomer" -> userServicePort.deleteUser(userId);
        }
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
