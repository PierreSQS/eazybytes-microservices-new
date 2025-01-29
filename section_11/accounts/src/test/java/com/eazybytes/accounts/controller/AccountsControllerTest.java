package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.service.IAccountsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    IAccountsService iAccountsServMock;

    CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customerDto = CustomerDto.builder()
                .name("Madan")
                .mobileNumber("1234567890")
                .email("madan@eazybytes.com")
                .build();
    }

    @Test
    void createAccount() throws Exception {
        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMsg")
                        .value("Account created successfully"))
                .andDo(print());

        verify(iAccountsServMock, times(1)).createAccount(customerDto);
    }

    @Test
    void fetchAccountDetails() throws Exception{
        // Given
        given(iAccountsServMock.fetchAccount(customerDto.getMobileNumber())).willReturn(customerDto);
        // When, Then
        mockMvc.perform(get("/api/fetch")
                        .param("mobileNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobileNumber")
                        .value("1234567890"))
                .andDo(print());
    }

    @Test
    void updateAccountDetails_OK() throws Exception {
        // Given
        given(iAccountsServMock.updateAccount(customerDto)).willReturn(true);

        mockMvc.perform(put("/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(customerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg")
                        .value(AccountsConstants.MESSAGE_200))
                .andDo(print());

        verify(iAccountsServMock, times(1)).updateAccount(customerDto);
    }

    @Test
    void updateAccountDetails_NOK() throws Exception {
        // Given
        given(iAccountsServMock.updateAccount(customerDto)).willReturn(false);

        mockMvc.perform(put("/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(customerDto)))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.statusMsg")
                        .value(AccountsConstants.MESSAGE_417_UPDATE))
                .andDo(print());

        verify(iAccountsServMock, times(1)).updateAccount(customerDto);
    }
}