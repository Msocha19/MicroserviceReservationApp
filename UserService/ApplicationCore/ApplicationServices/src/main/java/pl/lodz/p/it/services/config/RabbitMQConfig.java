package pl.lodz.p.it.services.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    static final String topicExchangeName = "tks-exchange";

    @Bean
    Queue queueEvent() {
        return new Queue("tks-event", false);
    }

    @Bean
    Queue queueResponse() {
        return new Queue("tks-response", false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding bindingEvent(Queue queueEvent, TopicExchange exchange) {
        return BindingBuilder.bind(queueEvent).to(exchange).with("event.#");
    }

    @Bean
    Binding bindingResponse(Queue queueResponse, TopicExchange exchange) {
        return BindingBuilder.bind(queueResponse).to(exchange).with("response.#");
    }

}
