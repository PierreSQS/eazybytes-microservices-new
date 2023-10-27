package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.service.IAccountsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountsControllerTest {

    @MockBean
    IAccountsService iAccountsSrvMock;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    CustomerDto customerDto;



    @BeforeEach
    void setUp() {
        customerDto = new CustomerDto();
        customerDto.setName("Test CustomerDTO");
        customerDto.setEmail("test@example.com");
        customerDto.setMobileNumber("1111111111");
    }

    @Test
    void createAccount() {
    }

    @Test
    void fetchAccountDetails() {
    }

    @Test
    void updateAccountDetailsUpdated() throws Exception {
        // Given
        given(iAccountsSrvMock.updateAccount(any(CustomerDto.class))).willReturn(Boolean.TRUE);

        mockMvc.perform(put("/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value(AccountsConstants.MESSAGE_200))
                .andDo(print());
    }

    @Test
    void updateAccountDetailsUpdatedFalse() throws Exception {
        // Given
        given(iAccountsSrvMock.updateAccount(any(CustomerDto.class))).willReturn(Boolean.FALSE);

        mockMvc.perform(put("/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.statusMsg").value(AccountsConstants.MESSAGE_417_UPDATE))
                .andDo(print());
    }

    @Test
    void updateAccountDetailsAccountNotFound() throws Exception {
        // Given
        ResourceNotFoundException resNFE = new ResourceNotFoundException("Account", "AccountNumber","1111111111");
        doThrow(resNFE).when(iAccountsSrvMock).updateAccount(customerDto);

        mockMvc.perform(put("/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.apiPath").value("uri=/api/update"))
                .andExpect(jsonPath("$.errorMessage").value(containsString("AccountNumber : '1111111111'")))
                .andDo(print());
    }
    @Test
    void updateAccountDetailsCustomerNotFound() throws Exception {
        // Given
        ResourceNotFoundException resNFE = new ResourceNotFoundException("Customer", "CustomerID","1111111111");
        doThrow(resNFE).when(iAccountsSrvMock).updateAccount(customerDto);

        mockMvc.perform(put("/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.apiPath").value("uri=/api/update"))
                .andExpect(jsonPath("$.errorMessage").value(containsString("CustomerID : '1111111111'")))
                .andDo(print());
    }

    @Test
    void deleteAccountDetails() {
    }
}