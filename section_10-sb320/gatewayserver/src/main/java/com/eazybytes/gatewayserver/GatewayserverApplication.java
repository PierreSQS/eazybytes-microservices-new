package com.eazybytes.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

	private static final String SEGMENT = "/${segment}";
	public static final String X_RESPONSE_TIME = "X-Response-Time";

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}


	@Bean
	public RouteLocator eazyBankRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(p -> p.path("/eazybank/accounts/**")
						.filters( f -> f.rewritePath("/eazybank/accounts/(?<segment>.*)",SEGMENT)
								.addResponseHeader(X_RESPONSE_TIME, LocalDateTime.now().toString()))
						.uri("lb://ACCOUNTS"))
				.route(p -> p.path("/eazybank/loans/**")
						.filters(f -> f.rewritePath("/eazybank/loans/(?<segment>.*)",SEGMENT)
								.addResponseHeader(X_RESPONSE_TIME, LocalDateTime.now().toString())
								.circuitBreaker(config -> config.setName("accountsCircuitBreaker")))
						.uri("lb://LOANS"))
				.route(p -> p.path("/eazybank/cards/**")
						.filters(f -> f.rewritePath("/eazybank/cards/(?<segment>.*)",SEGMENT)
								.addResponseHeader(X_RESPONSE_TIME, LocalDateTime.now().toString()))
						.uri("lb://CARDS"))
				.build();
	}

}
