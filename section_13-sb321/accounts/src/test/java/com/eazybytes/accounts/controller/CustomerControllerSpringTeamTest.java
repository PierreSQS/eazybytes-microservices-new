package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CardsDto;
import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.dto.LoansDto;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.service.ICustomersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
class CustomerControllerSpringTeamTest {

    public final static String HEADER_ERROR_MSG = "Required header 'eazybank-correlation-id' is not present.";

    MockMvc mockMvc;

    @MockBean
    ICustomersService customersServMock;

    CustomerDetailsDto customerDetailsDto;

    Customer customer;

    AccountsDto accountsDto;
    CardsDto cardsDto;

    LoansDto loansDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerController(customersServMock)).build();

        accountsDto =  new AccountsDto();
        accountsDto.setAccountType(AccountsConstants.SAVINGS);
        accountsDto.setAccountNumber(1234567890L);

        cardsDto = new CardsDto();
        cardsDto.setCardNumber("1234567890");
        cardsDto.setCardType("Credit Card");
        cardsDto.setMobileNumber("1111111111");

        loansDto = new LoansDto();
        loansDto.setLoanNumber("2222222222");
        loansDto.setLoanType("Home Loan");

        customer = new Customer();
        customer.setCustomerId(12121212121L);
        customer.setMobileNumber("1111111111");
        customer.setName("Test User");
        customer.setEmail("test@example.com");

        customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(accountsDto);
        customerDetailsDto.setCardsDto(cardsDto);
        customerDetailsDto.setLoansDto(loansDto);
    }

    @Test
    void fetchCustomerDetails_MobileNumber_and_Header_OK() throws Exception {
        // Given
        given(customersServMock.fetchCustomerDetails(anyString(), anyString())).willReturn(customerDetailsDto);

        mockMvc.perform(get("/api/fetchCustomerDetails")
                        .param("mobileNumber", customerDetailsDto.getMobileNumber())
                        .header("eazybank-correlation-id",UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(customerDetailsDto.getName()))
                .andExpect(jsonPath("accountsDto.accountNumber")
                        .value(customerDetailsDto.getAccountsDto().getAccountNumber()))
                .andExpect(jsonPath("loansDto.loanNumber")
                        .value(customerDetailsDto.getLoansDto().getLoanNumber()))
                .andDo(print());
    }
    @Test
    void fetchCustomerDetails_MobileNumber_OK_and_Header_Not_Present() throws Exception {
        // Given
        given(customersServMock.fetchCustomerDetails(anyString(), anyString())).willReturn(customerDetailsDto);

        MvcResult mvcResult = mockMvc.perform(get("/api/fetchCustomerDetails")
                        .param("mobileNumber", customerDetailsDto.getMobileNumber()))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("eazybank-correlation-id"))
                .andDo(print())
                .andReturn();

        String errorMessage = mvcResult.getResponse().getErrorMessage();

        assertThat(errorMessage).isEqualTo(HEADER_ERROR_MSG);

    }
}