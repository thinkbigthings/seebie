package org.thinkbigthings.boot.web;



import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.thinkbigthings.boot.dto.UserResource.REL_PASSWORD_UPDATE;
import static org.thinkbigthings.boot.web.IntegrationTestConstants.HOST;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.thinkbigthings.boot.dto.UserRegistration;
import org.thinkbigthings.boot.dto.UserResource;



public class UserIntegrationTest {

    private final static String currentUserUrl =  HOST + "/user/current";
    private final static ParameterizedTypeReference userResourceType = new ParameterizedTypeReference<UserResource>(){};
    private final static ParameterizedTypeReference userPageResourceType = new ParameterizedTypeReference<PagedResources<UserResource>>(){};

    private ParameterizedRestTemplate basicAuth;
    private ParameterizedRestTemplate admin;
    private ParameterizedRestTemplate noAuth;
   
    @Before
    public void setup() throws Exception {
        basicAuth = BasicRequestFactory.createParameterizedTemplate("user@app.com",  "password");
        admin     = BasicRequestFactory.createParameterizedTemplate("admin@app.com", "password");
        noAuth    = BasicRequestFactory.createParameterizedTemplate();
    }

    /**
     * url should look like "?page=1&size=40";
     * requests and responses are all 0-based
     * 
     * @throws Exception
     */
    @Test
    public void testSpecificUserPages() throws Exception {
        
        int pageSize = 25;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(HOST + "/user").queryParam("size", pageSize);
        ResponseEntity<PagedResources<UserResource>> response;
        PagedResources<UserResource> page;
        String url;
        
        // get the first 40, use to compare what you get with multiple smaller pages
        url = builder.replaceQueryParam("page", 0).replaceQueryParam("size", 40).toUriString();
        response = admin.getForEntity(url, userPageResourceType);
        page = response.getBody();
        List<UserResource> first40 = new ArrayList<>(page.getContent());
        
        // get some sub pages and compare to first big page
        // follow "next" links for pages 2-4, then follow "previous" links back
        url = builder.replaceQueryParam("page", 1).replaceQueryParam("size", 5).toUriString();
        assertTrue(url.contains("page=1"));
        assertTrue(url.contains("size=5"));
        response = admin.getForEntity(url, userPageResourceType);
        List<UserResource> second5 = new ArrayList<>(response.getBody().getContent());
        assertEquals(first40.get(5).getUsername(), second5.get(0).getUsername());

        
        url = response.getBody().getLink(Link.REL_NEXT).getHref();
        assertTrue(url.contains("page=2"));
        assertTrue(url.contains("size=5"));
        response = admin.getForEntity(url, userPageResourceType);
        List<UserResource> third5 = new ArrayList<>(response.getBody().getContent());
        assertEquals(first40.get(11).getUsername(), third5.get(1).getUsername());
        
        
        url = response.getBody().getLink(Link.REL_NEXT).getHref();
        assertTrue(url.contains("page=3"));
        assertTrue(url.contains("size=5"));
        response = admin.getForEntity(url, userPageResourceType);
        List<UserResource> fourth5 = new ArrayList<>(response.getBody().getContent());
        assertEquals(first40.get(19).getUsername(), fourth5.get(4).getUsername());
        
        
        url = response.getBody().getLink(Link.REL_PREVIOUS).getHref();
        assertTrue(url.contains("page=2"));
        assertTrue(url.contains("size=5"));
        response = admin.getForEntity(url, userPageResourceType);
        third5 = new ArrayList<>(response.getBody().getContent());
        assertEquals(first40.get(11).getUsername(), third5.get(1).getUsername());
        
        
        url = builder.replaceQueryParam("page", 1).replaceQueryParam("size", 5).toUriString();
        assertTrue(url.contains("page=1"));
        assertTrue(url.contains("size=5"));
        response = admin.getForEntity(url, userPageResourceType);
        second5 = new ArrayList<>(response.getBody().getContent());
        assertEquals(first40.get(5).getUsername(), second5.get(0).getUsername());
    }
    

    @Test
    public void testGetDefaultUserPage() throws Exception {

        ResponseEntity<PagedResources<UserResource>>retrieved = admin.getForEntity(HOST + "/user", userPageResourceType);
        PagedResources<UserResource> page = retrieved.getBody();
        
        
        assertEquals(20, page.getMetadata().getSize());  // default page size
        assertEquals(0, page.getMetadata().getNumber()); // first page, page index numbers are zero-based
        
        // use links to retrieve single specific resource
        UserResource firstResource = page.getContent().iterator().next();
        ResponseEntity<UserResource> firstUserLinkResponse = admin.getForEntity(firstResource.getLink(Link.REL_SELF).getHref(), userResourceType); 
        UserResource retrievedFromPage =  firstUserLinkResponse.getBody();
        assertEquals(firstResource.getUsername(), retrievedFromPage.getUsername());
    }
    
    @Test
    public void testGetExistingUser() throws Exception {

        ResponseEntity<UserResource> retrieved = basicAuth.getForEntity(currentUserUrl, userResourceType);
        UserResource user10 = retrieved.getBody();
        
        assertEquals(OK, retrieved.getStatusCode());
        assertEquals("user@app.com", user10.getUsername());
        
        ResponseEntity<UserResource> retrieved10 = basicAuth.getForEntity(retrieved.getBody().getLink(Link.REL_SELF).getHref(), userResourceType);

        assertEquals(OK, retrieved10.getStatusCode());
        assertEquals("user@app.com", retrieved10.getBody().getUsername());
    }
    
    @Test
    public void testUserCRUD() throws Exception {

        UserRegistration registration = new UserRegistration();
        String uniqueName = UUID.randomUUID().toString();
        registration.setDisplayName(uniqueName);
        registration.setPlaintextPassword("password");
        registration.setUserName(uniqueName + "@app.com");
       
        //////// CREATE
        
        // perform registration, be able to make new calls with that user's auth
        noAuth.postForEntity(HOST + "/user/register", registration, userResourceType);
        ParameterizedRestTemplate auth = BasicRequestFactory.createParameterizedTemplate(registration.getUserName(), registration.getPlaintextPassword());
        
        // call for current user data
        ResponseEntity<UserResource> currentUserResponse = auth.getForEntity(currentUserUrl, userResourceType);
        String selfLink = currentUserResponse.getBody().getLink(Link.REL_SELF).getHref();
        
        
        ////////// RETRIEVE
        
        // call for specific user
        ResponseEntity<UserResource> newlyCreated = auth.getForEntity(selfLink, userResourceType);
        UserResource newUser = newlyCreated.getBody();
        
        // make sure data is coming back for who you registered.
        assertEquals(registration.getUserName(), newUser.getUsername());
        assertEquals(registration.getDisplayName(), newUser.getDisplayName());
        
        ////////// UPDATE
        
        // update user on client
        UserResource requestUpdate = new UserResource("UPDATED_" + newUser.getUsername(), "UPDATED_" + newUser.getDisplayName(), newUser.getLink(Link.REL_SELF));
        
        // send updated data to server, use returned user data from the put
        ResponseEntity<UserResource> putResponse = auth.putForEntity(selfLink, requestUpdate, userResourceType);
        UserResource updatedFromPut = putResponse.getBody();
        
        // ensure the response from the put contains the updated data
        assertEquals(requestUpdate.getDisplayName(), updatedFromPut.getDisplayName());
        assertEquals(requestUpdate.getUsername(), updatedFromPut.getUsername());
        
        // make sure security works with the updated username for subsequent calls
        ParameterizedRestTemplate updatedAuth = BasicRequestFactory.createParameterizedTemplate(updatedFromPut.getUsername(), "password");
        ResponseEntity<UserResource> updatedUserResponse = updatedAuth.getForEntity(selfLink, userResourceType);
        UserResource updated = updatedUserResponse.getBody();

        // ensure the response from the get with new credentials also contains the updated data
        assertEquals(updatedFromPut.getDisplayName(), updated.getDisplayName());
        assertEquals(updatedFromPut.getUsername(), updated.getUsername());
        
        
        // make sure credentials containing old username doesn't work
        ResponseEntity<String> attempt = auth.getForEntity(selfLink, String.class);
        assertEquals(UNAUTHORIZED, attempt.getStatusCode());
        
    }

    @Test
    public void updatePassword() throws Exception {

        String originalPassword = "originalpassword";
        String newPassword = "newpassword";
        String uniqueName = UUID.randomUUID().toString();
        String userName = uniqueName + "@app.com";
        
        ParameterizedRestTemplate origAuth = BasicRequestFactory.createParameterizedTemplate(userName, originalPassword);
        ParameterizedRestTemplate newAuth = BasicRequestFactory.createParameterizedTemplate(userName, newPassword);

        UserRegistration registration = new UserRegistration();
        registration.setDisplayName(uniqueName);
        registration.setPlaintextPassword(originalPassword);
        registration.setUserName(userName);
        
        //////// CREATE
        
        // perform registration, be able to make new calls with that user's auth
        // then call for current user data
        noAuth.postForEntity(HOST + "/user/register", registration, userResourceType);
        ResponseEntity<UserResource> currentUserResponse = origAuth.getForEntity(currentUserUrl, UserResource.class);
        String passwordUpdateLink = currentUserResponse.getBody().getLink(REL_PASSWORD_UPDATE).getHref();
        
        ////////// UPDATE PASSWORD TO NEW
        
        ResponseEntity<UserResource> putResponse = origAuth.putForEntity(passwordUpdateLink, newPassword, userResourceType);
        UserResource updatedFromPut = putResponse.getBody();
        assertEquals(userName, updatedFromPut.getUsername());
        
        // make sure credentials containing old password doesn't work
        ResponseEntity<String> attempt = origAuth.putForEntity(passwordUpdateLink, newPassword, new ParameterizedTypeReference<String>(){});
        assertEquals(UNAUTHORIZED, attempt.getStatusCode());
        
        ////////// UPDATE PASSWORD TO ORIGINAL
        
        putResponse = newAuth.putForEntity(passwordUpdateLink, originalPassword, userResourceType);
        updatedFromPut = putResponse.getBody();
        currentUserResponse = origAuth.getForEntity(currentUserUrl, UserResource.class);
        
        assertEquals(userName, updatedFromPut.getUsername());
        assertEquals(userName, currentUserResponse.getBody().getUsername());

    }
    
    @Test
    public void updateWithInvalidRequestBody() throws Exception {
        
        ResponseEntity<UserResource> currentUserResponse = basicAuth.getForEntity(currentUserUrl, userResourceType);
        Link self = currentUserResponse.getBody().getLink(Link.REL_SELF);
        
        UserResource requestUpdate = new UserResource("", "", self);
        ResponseEntity<String> responseToAttempt = basicAuth.putForEntity(self.getHref(), requestUpdate, new ParameterizedTypeReference<String>(){});
        
        assertEquals(BAD_REQUEST, responseToAttempt.getStatusCode());
        assertTrue(responseToAttempt.getBody().contains("Validation of incoming object failed"));
    }

}
