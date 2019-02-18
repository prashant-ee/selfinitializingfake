package org.prashantkalkar.selfinitializingfake;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class GenericEndpoint {

    @Value("${target.app.base.url}")
    private String targetBaseUrl;

//    private static String TARGET_BASE_URL = "http://localhost:8089";

    // Can cause performance issues but should work for testing purposes.
    Map<Request, ResponseEntity<String>> requestCache = new ConcurrentHashMap<>();

    @RequestMapping(path = "/*")
    ResponseEntity<String> home(HttpServletRequest request, HttpEntity<String> requestEntity) {
        Optional<ResponseEntity<String>> responseOps = getResponseFromCache(request);
        return responseOps.orElseGet(() -> responseFromThirdParty(request, requestEntity));
    }

    private ResponseEntity<String> responseFromThirdParty(HttpServletRequest request, HttpEntity<String> requestEntity) {
        String requestPath = request.getRequestURI();
        System.out.println("Request URL: " + requestPath);
        System.out.println("Request method : " + request.getMethod());

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> thirdPartyRequestEntity = null;
        if(requestEntity.hasBody())
            thirdPartyRequestEntity = new HttpEntity<>(requestEntity.getBody());

        ResponseEntity<String> responseEntity = restTemplate.exchange(targetBaseUrl + requestPath, HttpMethod.valueOf(request.getMethod()), thirdPartyRequestEntity, String.class);
        requestCache.put(new Request(request), responseEntity);
        return responseEntity;
    }

    private Optional<ResponseEntity<String>> getResponseFromCache(HttpServletRequest request) {
        for(Map.Entry<Request, ResponseEntity<String>> entry : requestCache.entrySet()) {
            if(new Request(request).equals(entry.getKey())) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }
}
