package org.thinkbigthings.boot.web;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SleepDataIntegrationTest {

    @Test
    public void testUserAccessControl() throws Exception {
        RestTemplate basicAuth = BasicRequestFactory.createTemplate("user@app.com", "password");
        String HOST = "https://localhost:9000";
        String url =  HOST + "/user/10/sleep";
        ResponseEntity<String> response = basicAuth.getForEntity(url, String.class);
        assertEquals(OK, response.getStatusCode());
    }

}
