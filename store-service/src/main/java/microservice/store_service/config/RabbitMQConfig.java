package microservice.store_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String STORE_EVENTS_EXCHANGE = "store.events.exchange";
    public static final String STORE_STATUS_QUEUE = "store.status.queue";
    public static final String STORE_STATUS_ROUTING_KEY = "store.status.changed";

    @Bean
    public TopicExchange storeEventsExchange() {
        return new TopicExchange(STORE_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue storeStatusQueue() {
        return new Queue(STORE_STATUS_QUEUE, true); // durable
    }

    @Bean
    public Binding storeStatusBinding() {
        return BindingBuilder.bind(storeStatusQueue())
                .to(storeEventsExchange())
                .with(STORE_STATUS_ROUTING_KEY);
    }
}
