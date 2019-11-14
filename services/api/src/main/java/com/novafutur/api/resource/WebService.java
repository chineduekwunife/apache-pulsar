package com.novafutur.api.resource;

import com.novafutur.api.service.JobService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(WebService.RESOURCE)
@RequiredArgsConstructor
public class WebService {

    private Logger logger = LoggerFactory.getLogger(WebService.class);

    public static final String RESOURCE = "/ws";

    private final JobService jobService;

    @GetMapping("/{number}")
    public ResponseEntity test(@PathVariable("number") String number) {
        Integer value = Integer.valueOf(number);

        Object result = jobService.compute(value);

        return ResponseEntity.ok(result);
    }

}
