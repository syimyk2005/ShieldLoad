package shield.load.shieldapi.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import shield.load.shieldapi.dto.DecisionRequest;
import shield.load.shieldapi.event.RateLimitEvent;

import java.util.UUID;

@Service
public class RateLimitProducer {

    private final KafkaTemplate<String, RateLimitEvent> kafkaTemplate;

    public RateLimitProducer(KafkaTemplate<String, RateLimitEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String placeOrder(DecisionRequest decisionRequest) {
        String mockId = UUID.randomUUID().toString();

        RateLimitEvent rateLimitEvent = new RateLimitEvent(
                mockId,
                decisionRequest.endpoint(),
                decisionRequest.timestamp()

        );

        kafkaTemplate.send("rates", rateLimitEvent.clientId(), rateLimitEvent);
        return mockId;
    }
}