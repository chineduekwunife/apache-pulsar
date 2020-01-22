package com.pulsar.consumer.resource;

import com.pulsar.consumer.subscriber.shared.FirstSharedSubscriber;
import com.pulsar.consumer.subscriber.shared.SecondSharedSubscriber;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WebService.RESOURCE)
@RequiredArgsConstructor
public class WebService {

    private Logger logger = LoggerFactory.getLogger(WebService.class);

    public static final String RESOURCE = "/ws";

    private final FirstSharedSubscriber firstSharedSubscriber;
    private final SecondSharedSubscriber secondSharedSubscriber;

    @GetMapping("/pulsar")
    public ResponseEntity closeSubscribers() {
        firstSharedSubscriber.close();

        secondSharedSubscriber.close();

        return ResponseEntity.ok("");
    }
}
