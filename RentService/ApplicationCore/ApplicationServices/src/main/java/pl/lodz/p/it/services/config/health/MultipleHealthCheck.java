package pl.lodz.p.it.services.config.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.net.URL;

@Component("UserServiceConnectionCheck")
@RequiredArgsConstructor
public class MultipleHealthCheck implements HealthIndicator {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Health health() {
        try (Socket socket = new Socket(new URL("http://localhost:8081/customer").getHost(), 8081)) {
            AvailabilityChangeEvent.publish(this.eventPublisher, "successfull connection", ReadinessState.ACCEPTING_TRAFFIC);
        } catch (Exception e) {
            AvailabilityChangeEvent.publish(this.eventPublisher, e, ReadinessState.REFUSING_TRAFFIC);
            return Health.up().withDetail("UserServiceConnectionCheck: ",
                "Connection with UserService Failed!").withDetail("Error: ", e.getMessage()).build();
        }
        return Health.up().withDetail("UserServiceConnectionCheck: ", "Connection Success!").build();
    }
}
