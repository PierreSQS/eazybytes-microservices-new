// accounts/src/test/java/com/eazybytes/accounts/controller/AccountsControllerTest.java
package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsContactInfoDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.service.IAccountsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountsController.class)
class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAccountsService iAccountsService;

    @MockBean
    private Environment environment;

    @MockBean
    private AccountsContactInfoDto accountsContactInfoDto;

    // --- Good path tests ---

    @Test
    @DisplayName("POST /api/create - success")
    void createAccount_shouldReturnCreated() throws Exception {
        String customerJson = "{\"name\":\"John Doe\",\"mobileNumber\":\"1234567890\"}";
        mockMvc.perform(post("/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(AccountsConstants.STATUS_201));
    }

    @Test
    @DisplayName("GET /api/fetch - success")
    void fetchAccountDetails_shouldReturnOk() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setMobileNumber("1234567890");
        Mockito.when(iAccountsService.fetchAccount("1234567890")).thenReturn(customerDto);

        mockMvc.perform(get("/api/fetch")
                .param("mobileNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobileNumber").value("1234567890"));
    }

    @Test
    @DisplayName("PUT /api/update - success")
    void updateAccountDetails_shouldReturnOk() throws Exception {
        Mockito.when(iAccountsService.updateAccount(any(CustomerDto.class))).thenReturn(true);
        String customerJson = "{\"name\":\"John Doe\",\"mobileNumber\":\"1234567890\"}";

        mockMvc.perform(put("/api/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(AccountsConstants.STATUS_200));
    }

    @Test
    @DisplayName("DELETE /api/delete - success")
    void deleteAccountDetails_shouldReturnOk() throws Exception {
        Mockito.when(iAccountsService.deleteAccount("1234567890")).thenReturn(true);

        mockMvc.perform(delete("/api/delete")
                .param("mobileNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(AccountsConstants.STATUS_200));
    }

    @Test
    @DisplayName("GET /api/build-info - success")
    void getBuildInfo_shouldReturnOk() throws Exception {
        Mockito.when(environment.getProperty("build.version")).thenReturn("1.0.0");

        mockMvc.perform(get("/api/build-info"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/java-version - success")
    void getJavaVersion_shouldReturnOk() throws Exception {
        Mockito.when(environment.getProperty("JAVA_HOME")).thenReturn("Java 21");

        mockMvc.perform(get("/api/java-version"))
                .andExpect(status().isOk())
                .andExpect(content().string("Java 21"));
    }

    @Test
    @DisplayName("GET /api/contact-info - success")
    void getContactInfo_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/contact-info"))
                .andExpect(status().isOk());
    }

    // --- Edge/negative path tests ---

    @Test
    @DisplayName("POST /api/create - invalid payload")
    void createAccount_invalidPayload_shouldReturnBadRequest() throws Exception {
        String invalidJson = "{\"name\":\"\",\"mobileNumber\":\"abc\"}";
        mockMvc.perform(post("/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/fetch - invalid mobile number")
    void fetchAccountDetails_invalidMobile_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/fetch")
                .param("mobileNumber", "12345abcde"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/update - service fails")
    void updateAccountDetails_serviceFails_shouldReturnExpectationFailed() throws Exception {
        Mockito.when(iAccountsService.updateAccount(any(CustomerDto.class))).thenReturn(false);
        String customerJson = "{\"name\":\"John Doe\",\"mobileNumber\":\"1234567890\"}";

        mockMvc.perform(put("/api/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.statusCode").value(AccountsConstants.STATUS_417));
    }

    @Test
    @DisplayName("DELETE /api/delete - service fails")
    void deleteAccountDetails_serviceFails_shouldReturnExpectationFailed() throws Exception {
        Mockito.when(iAccountsService.deleteAccount("1234567890")).thenReturn(false);

        mockMvc.perform(delete("/api/delete")
                .param("mobileNumber", "1234567890"))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.statusCode").value(AccountsConstants.STATUS_417));
    }

    @Test
    @DisplayName("GET /api/build-info fallback")
    void getBuildInfoFallback_shouldReturnFallbackValue() {
        AccountsController controller = new AccountsController(iAccountsService, environment, accountsContactInfoDto);
        var response = controller.getBuildInfoFallback(new RuntimeException("Simulated"));
        assert response.getStatusCode().is2xxSuccessful();
        assert "0.9".equals(response.getBody());
    }

    @Test
    @DisplayName("GET /api/java-version fallback")
    void getJavaVersionFallback_shouldReturnFallbackValue() {
        AccountsController controller = new AccountsController(iAccountsService, environment, accountsContactInfoDto);
        var response = controller.getJavaVersionFallback(new RuntimeException("Simulated"));
        assert response.getStatusCode().is2xxSuccessful();
        assert "Java 21".equals(response.getBody());
    }
}