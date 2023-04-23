package pl.lodz.p.it.services.config.health;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.net.URL;

@Component("RentServiceConnectionCheck")
@RequiredArgsConstructor
public class MultipleHealthCheck implements HealthIndicator {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Health health() {
        try (Socket socket = new Socket(new URL("http://localhost:8080/product").getHost(), 8080)) {
            AvailabilityChangeEvent.publish(this.eventPublisher, "successfull connection", ReadinessState.ACCEPTING_TRAFFIC);
        } catch (Exception e) {
            AvailabilityChangeEvent.publish(this.eventPublisher, e, ReadinessState.REFUSING_TRAFFIC);
            return Health.up().withDetail("RentServiceConnectionCheck: ",
                "Connection with RentService Failed!").withDetail("Error: ", e.getMessage()).build();
        }
        return Health.up().withDetail("RentServiceConnectionCheck: ", "Connection Success!").build();
    }
}
