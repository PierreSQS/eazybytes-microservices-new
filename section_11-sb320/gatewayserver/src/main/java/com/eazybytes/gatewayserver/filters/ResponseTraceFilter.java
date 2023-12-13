package com.eazybytes.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

/**
 * Custom Filter for the Response Header
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ResponseTraceFilter {
    private final FilterUtility filterUtility;

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
            String correlationId = filterUtility.getCorrelationId(requestHeaders);
            log.debug("Updated the correlation id to the outbound headers: {}", correlationId);
            exchange.getResponse().getHeaders().add(FilterUtility.CORRELATION_ID, correlationId);
        }));
    }
}