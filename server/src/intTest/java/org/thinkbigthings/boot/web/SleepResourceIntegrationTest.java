package org.thinkbigthings.boot.web;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.thinkbigthings.boot.dto.SleepResource.DATE_FORMAT;
import static org.thinkbigthings.boot.web.IntegrationTestConstants.HOST;

import java.util.Collection;
import org.junit.Test;


import org.junit.Before;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.thinkbigthings.boot.dto.SleepResource;
import org.thinkbigthings.boot.dto.UserResource;

public class SleepResourceIntegrationTest {
    
    private final static String currentUserUrl =  HOST + "/user/current";
    private final static String url =  HOST + "/user/10/sleepresource";
    private final static ParameterizedTypeReference sleepPageResourceType = new ParameterizedTypeReference<PagedResources<SleepResource>>(){};

    
    private RestTemplate basicAuth;
    private ParameterizedRestTemplate basicParamAuth;
    
    @Before
    public void setup() throws Exception {
        basicParamAuth = BasicRequestFactory.createParameterizedTemplate("user@app.com", "password");
        basicAuth = BasicRequestFactory.createTemplate("user@app.com", "password");
    }

//    // test method that just inserts data
//    // inserts about 100 records per second
//    // gradle clean build intTest --tests org.thinkbigthings.boot.web.SleepResourceIntegrationTest.insertDummyData
//
//    private static Random rand = new Random(); 
//    // nextInt is normally exclusive of the top value,
//    // so add 1 to make it inclusive
//    private static int randInt(int min, int max) {
//        return rand.nextInt((max - min) + 1) + min;
//    }
//
//    @Test
//    public void insertDummyData() throws Exception {
//        DateTime start = DATE_TIME_FORMAT.parseDateTime("2012-06-04 05:30 AM EST");
//        DateTime finish = DATE_TIME_FORMAT.parseDateTime("2015-01-05 05:30 AM EST");
//        for (DateTime time = start; time.isBefore(finish); time = time.plusDays(1)  ) {
//            String curStart = DATE_TIME_FORMAT.print(time.minusMinutes(randInt(360, 510)));
//            String curFinish = DATE_TIME_FORMAT.print(time);
//            SleepResource newResource = new SleepResource(curStart, curFinish, 0, randInt(5, 30),  randInt(0, 1)*20);
//            basicAuth.postForEntity("/user/15/sleepresource", newResource, SleepResource.class);
//        }
//    }
    
    @Test
    public void testAllPagingQueries() throws Exception {
        ParameterizedRestTemplate basicAuth15 = BasicRequestFactory.createParameterizedTemplate("user0@app.com", "password");
        String user15 = HOST + "/user/15/sleepresource";
        ResponseEntity<PagedResources<SleepResource>> retrieved;
        
        // usual query will be desc
        // you can sort by any field, and asc/desc
        // https://github.com/spring-projects/spring-data-rest/wiki/Paging-and-Sorting
        retrieved = basicAuth15.getForEntity(user15+"?sort=timeOutOfBed,desc", sleepPageResourceType);
        Collection<SleepResource> page = retrieved.getBody().getContent();
        
        assertEquals(20, page.size());
        assertEquals("2015-01-04", page.iterator().next().getFinishTime().substring(0, 10));
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
    
    @Test
    public void testFollowLinks() throws Exception {

        ResponseEntity<UserResource> retrieved = basicAuth.getForEntity(currentUserUrl, UserResource.class);
        UserResource user10 = retrieved.getBody();
        
        String sleepUrl = user10.getLink(UserResource.REL_SLEEP).getHref();
        
        ResponseEntity<PagedResources<SleepResource>> sleepPage = basicParamAuth.getForEntity(sleepUrl, sleepPageResourceType);
        PageMetadata sleepPageMeta = sleepPage.getBody().getMetadata();
        Collection<SleepResource> page = sleepPage.getBody().getContent();
        
        assertEquals(2, sleepPageMeta.getTotalElements());
        assertEquals(sleepPageMeta.getTotalElements(), page.size());
        
    }
    
    // TODO 1 test for invalid request body
    
    // TODO 1 test for get of sleep that doesn't belong to a user

}
