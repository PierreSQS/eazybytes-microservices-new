package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.service.IAccountsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    @Captor
    ArgumentCaptor<CustomerDto> customerDtoArgCaptor;

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

        verify(iAccountsServMock, times(1)).createAccount(customerDtoArgCaptor.capture());
        assertThat(customerDtoArgCaptor.getValue()).isEqualTo(customerDto);
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

    @Test
    void deleteAccountDetails_OK() throws Exception{
        // Given
        given(iAccountsServMock.deleteAccount(customerDto.getMobileNumber()))
                .willReturn(true);

        // When, Then
        mockMvc.perform(delete("/api/delete")
                        .param("mobileNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg")
                        .value(AccountsConstants.MESSAGE_200))
                .andDo(print());
    }

    @Test
    void deleteAccountDetails_NOK() throws Exception{
        // Given
        given(iAccountsServMock.deleteAccount(customerDto.getMobileNumber()))
                .willReturn(false);

        // When, Then
        mockMvc.perform(delete("/api/delete")
                        .param("mobileNumber", "1234567890"))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.statusMsg")
                        .value(AccountsConstants.MESSAGE_417_DELETE))
                .andDo(print());
    }

    @Test
    void getBuildInfoActuator() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.version")
                        .value("1.0.0"))
                .andDo(print());
    }

    @Test
    void getBuildInfo() throws Exception {
        mockMvc.perform(get("/api/build-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("1.0"))
                .andDo(print());
    }

    @Test
    void getJavaVersion() throws Exception {
        mockMvc.perform(get("/api/java-version"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value(containsString("jdk-21.0.2")))
                .andDo(print());
    }
}