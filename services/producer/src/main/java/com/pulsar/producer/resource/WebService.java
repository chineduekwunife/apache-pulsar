package com.pulsar.producer.resource;

import com.pulsar.producer.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            producerService.sendMessage();
        }

        return ResponseEntity.ok("");
    }

}
