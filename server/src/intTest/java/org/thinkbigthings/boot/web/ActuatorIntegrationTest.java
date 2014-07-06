package org.thinkbigthings.boot.web;



import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.thinkbigthings.boot.web.BasicRequestFactory;

public class ActuatorIntegrationTest  {

    @Test
    public void testHealth() throws Exception {
        RestTemplate noAuth = BasicRequestFactory.createTemplate();

        ResponseEntity<String> response = noAuth.getForEntity("https://localhost:9000/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
    }

}
