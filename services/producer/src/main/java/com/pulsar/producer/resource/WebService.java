package com.pulsar.producer.resource;

import com.pulsar.model.SensorReading;
import com.pulsar.producer.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.pulsar.model.constants.Topic.MY_TOPIC;
import static com.pulsar.model.constants.Topic.MY_SECOND_TOPIC;
import static com.pulsar.model.constants.Topic.SENSOR_TOPIC;

@RestController
@RequestMapping(WebService.RESOURCE)
@RequiredArgsConstructor
public class WebService {

    private Logger logger = LoggerFactory.getLogger(WebService.class);

    public static final String RESOURCE = "/ws";

    private final ProducerService producerService;

    @Value("${message.bus.connect}")
    private Boolean messageBusConnect;

    @GetMapping("/pulsar")
    public ResponseEntity sendToMessageBus() {
        if (messageBusConnect) {
            producerService.sendMessage(new SensorReading(2.0F), SENSOR_TOPIC, JSONSchema.of(SensorReading.class));
            producerService.sendMessage("My message", MY_TOPIC, Schema.STRING);
            producerService.sendMessage("My second message", MY_SECOND_TOPIC, Schema.STRING);

        }

        return ResponseEntity.ok("");
    }

}
