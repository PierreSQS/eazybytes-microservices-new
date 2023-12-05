package com.eazybytes.accounts.service.client;

import com.eazybytes.accounts.dto.CardsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Same name as the application name
// registered in the Eureka Server
// probably as spring.application.name
// in application.yml file
@FeignClient("cards")
public interface CardsFeignClient {
    @GetMapping("api/fetch")
    ResponseEntity<CardsDto> fetchCardDetails(@RequestParam String mobileNumber);
}
