package com.ls.mercadopagogateway.Functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

public class RequestBodyRewrite implements RewriteFunction<String, String> {

    private final ObjectMapper objectMapper;
    public RequestBodyRewrite(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

    @Override
    public Publisher<String> apply(ServerWebExchange exchange, String body) {

        try {

            Map<String, Object> map = objectMapper.readValue(body, Map.class);

            map.put("test: ", "test");

            return Mono.just(objectMapper.writeValueAsString(map));
        } catch (Exception ex) {

// json Handling of operation exceptions
            return Mono.error(new Exception("json process fail", ex));
        }
    }
}
