package org.thinkbigthings.boot.web;


import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.thinkbigthings.boot.web.IntegrationTestConstants.HOST;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.thinkbigthings.boot.assembler.Page;
import org.thinkbigthings.boot.assembler.Resource;
import org.thinkbigthings.boot.domain.User;

// TODO 1 use links to trace through application states for link tests and hateoas test

public class UserIntegrationTest {

    private final static String currentUserUrl =  HOST + "/user/current";
    private final static ParameterizedTypeReference userResourceType = new ParameterizedTypeReference<Resource<User>>(){};
//    private final static ParameterizedTypeReference userPageResourceType = new ParameterizedTypeReference<String>(){};
    private ParameterizedRestTemplate basicAuth;
    private final static ParameterizedTypeReference userPageResourceType = new ParameterizedTypeReference<Page<User>>(){};
    private ParameterizedRestTemplate admin;
    
   
    @Before
    public void setup() throws Exception {
        basicAuth = BasicRequestFactory.createParameterizedTemplate("user@app.com",  "password");
        admin     = BasicRequestFactory.createParameterizedTemplate("admin@app.com", "password");
    }

    @Test
    public void testGetUserPage() throws Exception {

        ResponseEntity<Page<User>>retrieved = admin.getForEntity(HOST + "/user/all", userPageResourceType);
        Page<User> users = retrieved.getBody();
        
//        Assert.assertEquals("", users);
    }
    
    @Test
    public void testGetExistingUser() throws Exception {

        ResponseEntity<Resource<User>> retrieved = basicAuth.getForEntity(currentUserUrl, userResourceType);
        User user10 = retrieved.getBody().getContent();
        
        assertEquals(OK, retrieved.getStatusCode());
        assertTrue(10L == user10.getId());
        assertEquals("user@app.com", user10.getUsername());
        
        ResponseEntity<Resource<User>> retrieved10 = basicAuth.getForEntity(retrieved.getBody().getLink("self").getHref(), userResourceType);

        assertEquals(OK, retrieved10.getStatusCode());
        assertTrue(10L == retrieved10.getBody().getContent().getId());
        assertEquals("user@app.com", retrieved10.getBody().getContent().getUsername());
    }
    
    @Test
    public void testUserCRUD() throws Exception {

        ParameterizedRestTemplate noAuth = BasicRequestFactory.createParameterizedTemplate();

        User requestUser = new User();
        String uniqueName = UUID.randomUUID().toString();
        requestUser.setDisplayName(uniqueName);
        requestUser.setPassword("password");
        requestUser.setUsername(uniqueName + "@app.com");
       
        //////// CREATE
        
        // perform registration, be able to make new calls with that user's auth
        noAuth.postForEntity(HOST + "/user/register", requestUser, userResourceType);
        ParameterizedRestTemplate auth = BasicRequestFactory.createParameterizedTemplate(requestUser.getUsername(), requestUser.getPassword());
        
        // call for current user data
        ResponseEntity<Resource<User>> currentUserResponse = auth.getForEntity(currentUserUrl, userResourceType);
        String selfLink = currentUserResponse.getBody().getLink("self").getHref();
        
        
        ////////// RETRIEVE
        
        // call for specific user
        ResponseEntity<Resource<User>> newlyCreated = auth.getForEntity(selfLink, userResourceType);
        User newUser = newlyCreated.getBody().getContent();
        
        // make sure data is coming back for who you registered.
        assertEquals(requestUser.getUsername(), newUser.getUsername());
        assertEquals(requestUser.getDisplayName(), newUser.getDisplayName());
        assertTrue(newUser.getId() > 0);
        
        
        ////////// UPDATE
        
        // update user on client
        User requestUpdate = newUser;
        requestUpdate.setDisplayName("UPDATED_" + newUser.getDisplayName());
        requestUpdate.setUsername("UPDATED_" + newUser.getUsername());
        
        // send updated data to server, use returned user data from the put
        ResponseEntity<Resource<User>> putResponse = auth.putForEntity(selfLink, requestUpdate, userResourceType);
        User updatedFromPut = putResponse.getBody().getContent();
        
        // ensure the response from the put contains the updated data
        assertEquals(requestUpdate.getDisplayName(), updatedFromPut.getDisplayName());
        assertEquals(requestUpdate.getUsername(), updatedFromPut.getUsername());
        
        // make sure security works with the updated username for subsequent calls
        ParameterizedRestTemplate updatedAuth = BasicRequestFactory.createParameterizedTemplate(updatedFromPut.getUsername(), "password");
        ResponseEntity<Resource<User>> updatedUserResponse = updatedAuth.getForEntity(selfLink, userResourceType);
        User updated = updatedUserResponse.getBody().getContent();

        // ensure the response from the get with new credentials also contains the updated data
        assertEquals(updatedFromPut.getId(), updated.getId());
        assertEquals(updatedFromPut.getDisplayName(), updated.getDisplayName());
        assertEquals(updatedFromPut.getUsername(), updated.getUsername());
        
        
        // make sure old username credentials doesn't work
        ResponseEntity<Resource<User>> attempt = auth.getForEntity(selfLink, userResourceType);
        assertEquals(UNAUTHORIZED, attempt.getStatusCode());
        
        // TODO 1 be able to update user password, this should be a separate POST
    }

}
