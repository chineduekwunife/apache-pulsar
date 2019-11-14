package com.novafutur.api.resource;

import com.novafutur.api.support.BaseWebServiceTest;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.novafutur.api.resource.WebService.RESOURCE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebServiceTest extends BaseWebServiceTest {

    @Test
    public void compute() throws Exception {
        this.mvc.perform(get(RESOURCE + "/" + 100))
                .andExpect(status().isOk())
                .andExpect(content().string("we’re calculating the answer, please come back in an unpredictable time..."));

        this.mvc.perform(get(RESOURCE + "/" + 100))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));
    }
}
