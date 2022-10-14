package com.ls.mercadopagogateway.Filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR;

@Component
public class RequestBodyChangeGatewayFilterFactory extends
        AbstractGatewayFilterFactory<RequestBodyChangeGatewayFilterFactory.Config> {



    private final List<HttpMessageReader<?>> messageReaders =
            HandlerStrategies.withDefaults().messageReaders();

    public RequestBodyChangeGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> ServerWebExchangeUtils
                .cacheRequestBodyAndRequest(exchange, (httpRequest) -> ServerRequest
                        .create(exchange.mutate().request(httpRequest).build(),
                                messageReaders)
                        .bodyToMono(String.class)
                        .doOnNext(requestPayload -> exchange
                                .getAttributes()
                                .put("test", "test"))
                        .then(Mono.defer(() -> {
                            ServerHttpRequest cachedRequest = exchange.getAttribute(
                                    CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR);
                            Assert.notNull(cachedRequest,
                                    "cache request shouldn't be null");
                            exchange.getAttributes()
                                    .remove(CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR);

                            cachedRequest = cachedRequest.mutate()
                                    .build();
                            return chain.filter(exchange.mutate()
                                    .request(cachedRequest)
                                    .build());
                        })));
    }



    static class Config {

    }
}

