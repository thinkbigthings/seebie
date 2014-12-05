package org.thinkbigthings.boot.web;

import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import static org.springframework.http.HttpStatus.OK;

import org.junit.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.thinkbigthings.sleep.SleepSessionDaily;

public class SleepDataIntegrationTest {

    private final String HOST = "https://localhost:9000";
    private final String url =  HOST + "/user/10/sleep";
    private RestTemplate basicAuth;
    
    @Before
    public void setup() throws Exception {
        basicAuth = BasicRequestFactory.createTemplate("user@app.com", "password");
    }
        
    @Test
    public void testCreateSleepSession() throws Exception {
        SleepSessionDaily entity = new SleepSessionDaily("2014-07-03 05:30 AM EST", 480, 0,  0);
        ResponseEntity<String> response = basicAuth.postForEntity(url, entity, String.class);
                
        assertEquals(TRUE.toString(), response.getBody());
        assertEquals(OK, response.getStatusCode());
    }
    
    @Test
    public void testUserAccessControl() throws Exception {
        ResponseEntity<String> response = basicAuth.getForEntity(url, String.class);
        assertEquals(OK, response.getStatusCode());
    }

}
