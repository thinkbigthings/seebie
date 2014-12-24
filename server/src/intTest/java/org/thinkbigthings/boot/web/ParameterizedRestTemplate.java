
package org.thinkbigthings.boot.web;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Alternative is to just use RestTemplate's .exchange(currentUserUrl, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<Concrete<ClassType>>(){});
 */
public class ParameterizedRestTemplate {
    private RestTemplate rt;

    public ParameterizedRestTemplate(RestTemplate t) {
        rt = t;
    }

    public <T> ResponseEntity<T> getForEntity(String url, Class<T> clazz) {
        return rt.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, clazz);
    }
    
    public <T> ResponseEntity<T> getForEntity(String url, ParameterizedTypeReference<T> responseType) {
        return rt.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, responseType);
    }
    
    public <T> ResponseEntity<T> postForEntity(String url, Object request, ParameterizedTypeReference<T> responseType) {
        return rt.exchange(url, HttpMethod.POST, new HttpEntity(request), responseType);
    }
    
    public <T> ResponseEntity<T> putForEntity(String url, Object request, ParameterizedTypeReference<T> responseType) {
        return rt.exchange(url, HttpMethod.PUT, new HttpEntity(request), responseType);
    }
}
