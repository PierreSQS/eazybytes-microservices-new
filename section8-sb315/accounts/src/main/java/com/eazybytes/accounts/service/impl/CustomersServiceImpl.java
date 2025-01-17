package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CardsDto;
import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.dto.LoansDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.ICustomersService;
import com.eazybytes.accounts.service.client.CardsFeignClient;
import com.eazybytes.accounts.service.client.LoansFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private final AccountsRepository accountsRepo;
    private final CustomerRepository customerRepo;
    private final CardsFeignClient cardsFeignClient;
    private final LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber) {
        // find Customer by mobile Number
        Customer foundCustomer = customerRepo.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        // find Account by customerID
        Accounts foundAccount = accountsRepo.findByCustomerId(foundCustomer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", foundCustomer.getCustomerId().toString())
        );

        // convert to Dtos
        AccountsDto accountsDto = AccountsMapper.mapToAccountsDto(foundAccount, new AccountsDto());

        // Request Card Details from Cards-µService
        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(mobileNumber);

        // Get CardDto
        CardsDto cardDtoFromService = cardsDtoResponseEntity.getBody();

        // Request Loans Details from Loans-µService
        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(mobileNumber);

        // Get LoansDto
        LoansDto loansDtoFromService = loansDtoResponseEntity.getBody();

        // Set CustomerDetails
        CustomerDetailsDto customerDetailsDto =
                CustomerMapper.mapToCustomerDetailsDto(foundCustomer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(accountsDto);
        customerDetailsDto.setCardsDto(cardDtoFromService);
        customerDetailsDto.setLoansDto(loansDtoFromService);

        return customerDetailsDto;
    }
}
