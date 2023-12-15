package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.client.CardsFeignClient;
import com.eazybytes.accounts.service.client.LoansFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class CustomersServiceImplTest {

    public static final String MOBILE_NR = "1111111111";
    @Mock
    AccountsRepository accountsRepo;
    @Mock
    CustomerRepository customerRepo;
    @Mock
    CardsFeignClient cardsFeignClient;
    @Mock
    LoansFeignClient loansFeignClient;

    CustomersServiceImpl customersServ;

    Customer customer;

    Accounts accounts;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer(1L,"test customer",
                "testcustomer@example", MOBILE_NR);

        accounts = new Accounts(1L,123456789L,
                "Home Loan","Wall Street");

        customersServ =
                new CustomersServiceImpl(accountsRepo, customerRepo, cardsFeignClient, loansFeignClient);

    }

    @Test
    void fetchCustomerDetails() {
        // Given
        given(customerRepo.findByMobileNumber(anyString())).willReturn(Optional.of(customer));

        given(accountsRepo.findByCustomerId(anyLong())).willReturn(Optional.of(accounts));

        CustomerDetailsDto customerDetailsDto =
                customersServ.fetchCustomerDetails(MOBILE_NR,
                        "35d2e375-13e2-497e-8ecf-d00d2ae3301b");

        assertThat(customerDetailsDto.getMobileNumber()).isEqualTo(MOBILE_NR);

        System.out.println(customerDetailsDto);
    }
}