package org.thinkbigthings.boot.web;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.thinkbigthings.boot.web.IntegrationTestConstants.HOST;

import org.junit.Test;


import org.junit.Before;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.thinkbigthings.boot.dto.SleepResource;

public class SleepResourceIntegrationTest {
    
    private final static ParameterizedTypeReference sleepPageResourceType = new ParameterizedTypeReference<PagedResources<SleepResource>>(){};
    
    private final String url =  HOST + "/user/10/sleepresource";
    private RestTemplate basicAuth;
    private ParameterizedRestTemplate basicParamAuth;
    
   
    @Before
    public void setup() throws Exception {
        basicParamAuth = BasicRequestFactory.createParameterizedTemplate("user@app.com", "password");
        basicAuth = BasicRequestFactory.createTemplate("user@app.com", "password");
    }
    
    @Test
    public void testGetPage() throws Exception {
        ResponseEntity<PagedResources<SleepResource>>retrieved = basicParamAuth.getForEntity(url, sleepPageResourceType);
        PagedResources<SleepResource> page = retrieved.getBody();
        
        assertEquals(2, page.getContent().size());
        assertEquals(20, page.getMetadata().getSize());  // default page size
        assertEquals(0, page.getMetadata().getNumber()); // first page, page index numbers are zero-based
    }

    @Test
    public void testSleepSessionWorkflow() throws Exception {

        // CREATE
        SleepResource newResource = new SleepResource("2014-07-03 09:30 PM EST", "2014-07-04 05:30 AM EST", 0, 25,  20);
        ResponseEntity<SleepResource> created = basicAuth.postForEntity(url, newResource, SleepResource.class);
        SleepResource newSleepResource = created.getBody();
        String sleepUrl = newSleepResource.getLink("self").getHref();
        
        assertEquals(OK, created.getStatusCode());
        assertEquals(newResource.getMinutesInBed(), newSleepResource.getMinutesInBed());
        
        // RETRIEVE
        ResponseEntity<SleepResource> response = basicAuth.getForEntity(sleepUrl, SleepResource.class);
        
        assertEquals(OK, response.getStatusCode());
        assertEquals(newSleepResource.getFinishTime(), response.getBody().getFinishTime());
        
        // UPDATE
        SleepResource updateRequest = new SleepResource("2014-07-03 09:30 PM EST", "2014-07-04 05:30 AM EST", 0, 35,  10);
        basicAuth.put(sleepUrl, updateRequest);
        ResponseEntity<SleepResource> retrieveAfterUpdate = basicAuth.getForEntity(sleepUrl, SleepResource.class);
        
        assertEquals(sleepUrl, retrieveAfterUpdate.getBody().getLink("self").getHref());
        assertEquals(updateRequest.getMinutesAwakeInBed(), retrieveAfterUpdate.getBody().getMinutesAwakeInBed());
        assertEquals(updateRequest.getMinutesAwakeNotInBed(), retrieveAfterUpdate.getBody().getMinutesAwakeNotInBed());
        
        // DELETE
        basicAuth.delete(sleepUrl);
        ResponseEntity<String> retrieveAfterDelete = basicAuth.getForEntity(sleepUrl, String.class);

        assertEquals(NOT_FOUND, retrieveAfterDelete.getStatusCode());
    }
    
    // TODO 1 test for invalid request body
    
    // TODO 1 test for get of sleep that doesn't belong to a user

}
