package org.thinkbigthings.boot.web;

import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(Parameterized.class)
public class UserAccessControlTest {

    private final RestTemplate auth;
    private final String url;
    private final HttpStatus expectedResponse;


    @Parameters
    public static Collection<Object[]> createUrlUserData() throws Exception {
        RestTemplate noAuth = BasicRequestFactory.createTemplate();
        RestTemplate basicAuth = BasicRequestFactory.createTemplate("user@app.com", "password");
        RestTemplate badAuth = BasicRequestFactory.createTemplate("user@app.com", "wrong password");
        RestTemplate adminAuth = BasicRequestFactory.createTemplate("admin@app.com", "password");
        String HOST = "https://localhost:9000";
        return Arrays.asList(new Object[][]{
            { noAuth,    HOST+"/user/current", UNAUTHORIZED },
            { badAuth,   HOST+"/user/current", UNAUTHORIZED },
            { basicAuth, HOST+"/user/current", OK },
            { adminAuth, HOST+"/user/current", OK},
            { noAuth,    HOST+"/user/all", UNAUTHORIZED },
            { badAuth,   HOST+"/user/all", UNAUTHORIZED },
            { basicAuth, HOST+"/user/all", UNAUTHORIZED },
            { adminAuth, HOST+"/user/all", OK},
            { noAuth,    HOST+"/user/10",  UNAUTHORIZED },
            { badAuth,   HOST+"/user/10",  UNAUTHORIZED },
            { basicAuth, HOST+"/user/10",  OK },
            { adminAuth, HOST+"/user/10",  OK},
            { noAuth,    HOST+"/user/11",  UNAUTHORIZED },
            { badAuth,   HOST+"/user/11",  UNAUTHORIZED },
            { basicAuth, HOST+"/user/11",  UNAUTHORIZED },
            { adminAuth, HOST+"/user/11",  OK}
        });
    }

    public UserAccessControlTest(RestTemplate template, String callUrl, HttpStatus expected) {
        auth = template;
        url = callUrl;
        expectedResponse = expected;
    }

    @Test
    public void testUserAccessControl() throws Exception {
        ResponseEntity<String> response = auth.getForEntity(url, String.class);
        assertEquals(expectedResponse, response.getStatusCode());
    }

}
