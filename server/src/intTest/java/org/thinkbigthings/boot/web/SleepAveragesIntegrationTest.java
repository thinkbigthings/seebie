package org.thinkbigthings.boot.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.thinkbigthings.boot.web.IntegrationTestConstants.HOST;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


import org.junit.Before;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.thinkbigthings.boot.dto.SleepAveragesResource;

public class SleepAveragesIntegrationTest {
    
    private final static String baseUrl =  HOST + "/user/15/sleepresource/averages";
    private final static ParameterizedTypeReference sleepAveragesResourceType = new ParameterizedTypeReference<PagedResources<SleepAveragesResource>>(){};

    private ParameterizedRestTemplate basicParamAuth;
    
    @Before
    public void setup() throws Exception {
        basicParamAuth = BasicRequestFactory.createParameterizedTemplate("user0@app.com", "password");
    }

    @Test
    public void testFollowLinks() throws Exception {
        
        String url = baseUrl + "?sort=groupEnding,desc&groupSize=WEEK";

        ResponseEntity<PagedResources<SleepAveragesResource>> retrieved;
        
        retrieved = basicParamAuth.getForEntity(url, sleepAveragesResourceType);
        url = retrieved.getBody().getNextLink().getHref();
        assertTrue(url.contains("page=1"));
        
        retrieved = basicParamAuth.getForEntity(url, sleepAveragesResourceType);
        url = retrieved.getBody().getNextLink().getHref();
        assertTrue(url.contains("page=2"));
        
        retrieved = basicParamAuth.getForEntity(url, sleepAveragesResourceType);
        url = retrieved.getBody().getNextLink().getHref();
        assertTrue(url.contains("page=3"));
        
        retrieved = basicParamAuth.getForEntity(url, sleepAveragesResourceType);
        url = retrieved.getBody().getPreviousLink().getHref();
        assertTrue(url.contains("page=2"));        
        
        retrieved = basicParamAuth.getForEntity(url, sleepAveragesResourceType);
        url = retrieved.getBody().getPreviousLink().getHref();
        assertTrue(url.contains("page=1"));  
        
        retrieved = basicParamAuth.getForEntity(url, sleepAveragesResourceType);
        url = retrieved.getBody().getPreviousLink().getHref();
        assertTrue(url.contains("page=0"));  
    }
    
    @Test
    public void testAllAverages() throws Exception {
        ResponseEntity<PagedResources<SleepAveragesResource>> retrieved = basicParamAuth.getForEntity(baseUrl + "?sort=groupEnding,desc&groupSize=ALL", sleepAveragesResourceType);
        PagedResources<SleepAveragesResource> page = retrieved.getBody();
        
        assertEquals(1, page.getMetadata().getTotalElements());
    }
    
    @Test
    public void testYearlyAverages() throws Exception {
        ResponseEntity<PagedResources<SleepAveragesResource>> retrieved = basicParamAuth.getForEntity(baseUrl + "?sort=groupEnding,desc&groupSize=YEAR", sleepAveragesResourceType);
        PagedResources<SleepAveragesResource> page = retrieved.getBody();
        List<SleepAveragesResource> data = new ArrayList<>(page.getContent());
        
        assertEquals(4, page.getMetadata().getTotalElements());
        
        // ensure descending
        assertTrue(data.get(0).getTimeOutOfBed().isAfter(data.get(1).getTimeOutOfBed()));
    }
    
    @Test
    public void testMonthlyAverages() throws Exception {
        ResponseEntity<PagedResources<SleepAveragesResource>> retrieved = basicParamAuth.getForEntity(baseUrl + "?sort=groupEnding,desc&groupSize=MONTH", sleepAveragesResourceType);
        PagedResources<SleepAveragesResource> page = retrieved.getBody();
        List<SleepAveragesResource> data = new ArrayList<>(page.getContent());
        
        assertEquals(32, page.getMetadata().getTotalElements());
        
        // ensure descending
        assertTrue(data.get(0).getTimeOutOfBed().isAfter(data.get(1).getTimeOutOfBed()));
    }
    
    @Test
    public void testGetPageDefaults() throws Exception {
        ResponseEntity<PagedResources<SleepAveragesResource>> retrieved = basicParamAuth.getForEntity(baseUrl, sleepAveragesResourceType);
        PagedResources<SleepAveragesResource> page = retrieved.getBody();
        List<SleepAveragesResource> data = new ArrayList<>(page.getContent());
        
        // defaults are group by week ending on sunday, descending by date
        assertEquals(135, page.getMetadata().getTotalElements());
        
        // ensure descending
        assertTrue(data.get(0).getTimeOutOfBed().isAfter(data.get(1).getTimeOutOfBed()));
    }

    @Test
    public void testGetPageDesc() throws Exception {

        ResponseEntity<PagedResources<SleepAveragesResource>> retrieved = basicParamAuth.getForEntity(baseUrl + "?sort=groupEnding,desc", sleepAveragesResourceType);
        PagedResources<SleepAveragesResource> page = retrieved.getBody();
        List<SleepAveragesResource> data = new ArrayList<>(page.getContent());
        
        // ensure ascending
        assertTrue(data.get(0).getTimeOutOfBed().isAfter(data.get(1).getTimeOutOfBed()));
    }
    
    @Test
    public void testGetPageAsc() throws Exception {

        ResponseEntity<PagedResources<SleepAveragesResource>> retrieved = basicParamAuth.getForEntity(baseUrl + "?sort=groupEnding,asc", sleepAveragesResourceType);
        PagedResources<SleepAveragesResource> page = retrieved.getBody();
        List<SleepAveragesResource> data = new ArrayList<>(page.getContent());
        
        // ensure ascending
        assertTrue(data.get(1).getTimeOutOfBed().isAfter(data.get(0).getTimeOutOfBed()));
    }
}
