package org.thinkbigthings.boot.web;

import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.junit.Test;

import static org.springframework.http.HttpStatus.OK;
import static org.thinkbigthings.boot.domain.Sleep.DATE_TIME_FORMAT;
import static org.thinkbigthings.boot.web.IntegrationTestConstants.HOST;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.sleep.SleepSessionJSON;

public class SleepDataIntegrationTest {
    
    private final String url =  HOST + "/user/10/sleep";
    private RestTemplate basicAuth;
    
    @Before
    public void setup() throws Exception {
        basicAuth = BasicRequestFactory.createTemplate("user@app.com", "password");
        
        
        // can log request/response traffic directly here
        // although the output of the test is logging it for me automatically.
        // there are other approaches too: http://stackoverflow.com/questions/7952154/spring-resttemplate-how-to-enable-full-debugging-logging-of-requests-responses/22620168#22620168
        basicAuth.setInterceptors(Arrays.asList((ClientHttpRequestInterceptor) (HttpRequest hr, byte[] bytes, ClientHttpRequestExecution chre) -> {
            // System.out.println(hr.getHeaders().toSingleValueMap());
            // body in the bytes
            ClientHttpResponse response = chre.execute(hr, bytes);
            // can get response body from stream here...
            return response;
        }));
    }
        
    @Test
    public void testSleepSessionWorkflow() throws Exception {

        // CREATE
        SleepSessionJSON newSession = new SleepSessionJSON("2014-07-04 05:30 AM EST", 480, 25,  20);
        ResponseEntity<Sleep> created = basicAuth.postForEntity(url, newSession, Sleep.class);
        Long createdId = created.getBody().getId();
        String sleepUrl = url + "/" + createdId;
                
        assertEquals(OK, created.getStatusCode());
        assertTrue(createdId > 0);
        assertEquals(newSession.getTimeOutOfBed(), DATE_TIME_FORMAT.withZone(UTC).print(created.getBody().getEndAsDateTime()));
        
        // RETRIEVE
        ResponseEntity<Sleep> retrieved = basicAuth.getForEntity(sleepUrl, Sleep.class);
        
        assertEquals(OK, retrieved.getStatusCode());
        assertEquals(createdId, retrieved.getBody().getId());
        
        // UPDATE
        SleepSessionJSON updateRequest = new SleepSessionJSON("2014-07-04 05:30 AM EST", 480, 35,  10);
        basicAuth.put(sleepUrl, updateRequest);
        ResponseEntity<Sleep> retrieveAfterUpdate = basicAuth.getForEntity(sleepUrl, Sleep.class);
        
        assertEquals(createdId, retrieveAfterUpdate.getBody().getId());
        assertEquals(updateRequest.getMinutesAwakeInBed(), retrieveAfterUpdate.getBody().getMinutesAwakeInBed());
        assertEquals(updateRequest.getMinutesAwakeNotInBed(), retrieveAfterUpdate.getBody().getMinutesAwakeNotInBed());
        
        // DELETE
        basicAuth.delete(sleepUrl);
        ResponseEntity<String> retrieveAfterDelete = basicAuth.getForEntity(sleepUrl, String.class);
       
        assertEquals(NOT_FOUND, retrieveAfterDelete.getStatusCode());
    }

    // TODO 1 test for invalid request body
    
    @Test
    public void testGetSleepSessions() throws Exception {

        ResponseEntity<List> response = basicAuth.getForEntity(url, List.class);

        assertTrue(response.getBody().size() >= 2);
        
        assertEquals(OK, response.getStatusCode());
    }

}
