package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.service.ICustomersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ICustomersService customersServMock;

    CustomerDetailsDto customerDetailsDto;

    @BeforeEach
    void setUp() {
        customerDetailsDto = new CustomerDetailsDto();
    }

    @Test
    void fetchCustomerDetails_MobileNumber_OK() throws Exception {
        mockMvc.perform(get("/api/fetchCustomerDetails").param("mobileNumber","1111111111"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}