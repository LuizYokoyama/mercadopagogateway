package com.ls.mercadopagogateway.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ls.mercadopagogateway.Functions.RequestBodyRewrite;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@Configuration
public class FilterConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, ObjectMapper objectMapper) {

        return builder
                .routes()
                .route("path_route_change",
                        r -> r.path("/mercado-pago/link-pagamento")
                                .filters(f -> f.addRequestHeader("Authorization", "Bearer TOKEN")
                                        .rewritePath("/mercado-pago/link-pagamento", "/checkout/preferences")
                                        .modifyRequestBody(String.class, String.class, MediaType.APPLICATION_JSON_VALUE,
                                                new RequestBodyRewrite(objectMapper))
                                )
                                .uri("https://api.mercadopago.com/"))
                .build();
    }
}
