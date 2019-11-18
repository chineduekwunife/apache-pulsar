package com.pulsar.producer.resource;

import com.pulsar.producer.support.BaseWebServiceTest;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.pulsar.producer.resource.WebService.RESOURCE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebServiceTest extends BaseWebServiceTest {

    @Test
    public void compute() throws Exception {
        this.mvc.perform(get(RESOURCE + "/pulsar"))
                .andExpect(status().isOk());
    }
}
