package com.doruk.infrastructure.messaging.publisher;

import com.doruk.application.interfaces.EventDto;
import com.doruk.application.interfaces.EventPublisher;
import io.micronaut.context.annotation.Bean;
import io.micronaut.json.JsonMapper;
import io.nats.client.Connection;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Singleton
@Bean(typed = EventPublisher.class)
@RequiredArgsConstructor
public class NatsEventPublisher implements EventPublisher {
    private final Connection natsConnection;
    private final JsonMapper jsonMapper;


    @Override
    public void publish(EventDto eventDto) {
        try {
            natsConnection.publish(eventDto.eventSubject(),
                    jsonMapper.writeValueAsBytes(eventDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
