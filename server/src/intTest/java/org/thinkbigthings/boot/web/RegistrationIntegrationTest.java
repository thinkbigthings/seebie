package org.thinkbigthings.boot.web;

import java.util.UUID;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.thinkbigthings.boot.domain.User;

public class RegistrationIntegrationTest {

    @Test
    public void testRegistration() throws Exception {

        RestTemplate noAuth = BasicRequestFactory.createTemplate();

        User requestUser = new User();
        String uniqueName = UUID.randomUUID().toString();
        requestUser.setDisplayName(uniqueName);
        requestUser.setPassword("password");
        requestUser.setUsername(uniqueName + "@app.com");
        
        ResponseEntity<User> response = noAuth.postForEntity("https://localhost:9000/register", requestUser, User.class);
        User userResponse = response.getBody();
        
        assertEquals(requestUser.getUsername(), userResponse.getUsername());
        assertTrue(userResponse.getId() > 0);


    }

}
