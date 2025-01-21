package com.seebie.server.test.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.function.Function;

import static com.seebie.server.function.Functional.uncheck;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;

/**
 * This class assembles all the information necessary for an HTTP request and bundles it with an expected http
 * response code so that they can be used in a parameterized WebMvcTest
 */
public class ArgumentsBuilder {

    private Function<Object, String> bodyMapper;

    public ArgumentsBuilder(ObjectMapper mapper) {
        // If the test data is a string, presume it is already in the correct format and return directly.
        // Because if you pass a string "" to the object mapper, it doesn't return the string, it returns """".
        this.bodyMapper = uncheck(obj -> obj instanceof String testData
                ? testData
                : mapper.writerFor(obj.getClass()).writeValueAsString(obj)
        );
    }

    public RequestBuilder toMvcRequest(HttpMethod method, String urlPath, Object reqBody) {
        return toMvcRequest(method, urlPath, reqBody, List.of());
    }

    public RequestBuilder toMvcRequest(HttpMethod method, String urlPath, Object reqBody, List<String> reqParams) {
        var params = toParams(reqParams);
        var builder = reqBody instanceof MockMultipartFile multipartFile
                ? multipart(urlPath).file(multipartFile)
                : request(method, urlPath).content(bodyMapper.apply(reqBody)).params(params).contentType(APPLICATION_JSON);
        return builder.secure(true);
    }

    /**
     * Works the same as HttpRequest.headers()
     * Do not need to url encode the parameters.
     *
     * @param newReqParams
     * @return
     */
    private static MultiValueMap<String, String> toParams(List<String> newReqParams) {
        if (newReqParams.size() % 2 != 0) {
            throw new IllegalArgumentException("Number of args must be even");
        }
        var newParams = new LinkedMultiValueMap<String, String>();
        for (int i = 0; i < newReqParams.size(); i += 2) {
            newParams.add(newReqParams.get(i), newReqParams.get(i + 1));
        }
        return unmodifiableMultiValueMap(newParams);
    }
}
