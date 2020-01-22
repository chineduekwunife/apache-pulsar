package com.pulsar.producer.resource;

import com.pulsar.admin.PulsarAdminService;
import com.pulsar.model.SensorReading;
import com.pulsar.producer.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.pulsar.model.constants.Topic.MY_TOPIC;
import static com.pulsar.model.constants.Topic.MY_SECOND_TOPIC;
import static com.pulsar.model.constants.Topic.STREAMING_TOPIC;
import static com.pulsar.model.constants.Topic.SENSOR_TOPIC;

@RestController
@RequestMapping(WebService.RESOURCE)
@RequiredArgsConstructor
public class WebService {

    private Logger logger = LoggerFactory.getLogger(WebService.class);

    public static final String RESOURCE = "/ws";
    private static final String APP = "sample-app";

    private final ProducerService producerService;
    private final PulsarAdminService pulsarAdminService;

    @Value("${message.bus.connect}")
    private Boolean messageBusConnect;

    @GetMapping("/pulsar")
    public ResponseEntity sendToMessageBus() throws PulsarAdminException {
        if (messageBusConnect) {
            producerService.sendMessage(new SensorReading(2.0F), pulsarAdminService.topic(SENSOR_TOPIC, APP), JSONSchema.of(SensorReading.class));
            producerService.sendMessage("My message", pulsarAdminService.topic(MY_TOPIC, APP), Schema.STRING);
            producerService.sendMessage("My second message", pulsarAdminService.topic(MY_SECOND_TOPIC, APP), Schema.STRING);
        }

        return ResponseEntity.ok("");
    }

    @GetMapping("/pulsar-tenants")
    public ResponseEntity getTenants() throws PulsarAdminException {
        if (messageBusConnect) {
            return ResponseEntity.ok().body(pulsarAdminService.tenants());
        }

        return ResponseEntity.ok("");
    }

    @GetMapping("/pulsar-clusters")
    public ResponseEntity getClusters() throws PulsarAdminException {
        if (messageBusConnect) {
            return ResponseEntity.ok().body(pulsarAdminService.clusters());
        }

        return ResponseEntity.ok("");
    }

    @GetMapping("/pulsar-topic")
    public ResponseEntity getTopic() throws PulsarAdminException {
        if (messageBusConnect) {
            String topic = pulsarAdminService.topic("hello", "test-app");
            producerService.sendMessage("My message", topic, Schema.STRING);
            return ResponseEntity.ok().body(topic);
        }

        return ResponseEntity.ok("");
    }

    @GetMapping("/pulsar-spark-stream")
    public ResponseEntity sendToSparkStream() {
        if (messageBusConnect) {
            for (int i = 1; i < 6; i++) {
                producerService.sendMessage("Hello-" + i, STREAMING_TOPIC, Schema.STRING);
            }
        }

        return ResponseEntity.ok("");
    }

}
