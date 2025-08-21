package com.eazybytes.accounts.mapper;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.entity.Accounts;

public class AccountsMapper {

    private AccountsMapper() {
        // Private constructor to prevent instantiation
    }

    public static AccountsDto mapToAccountsDto(Accounts accounts, AccountsDto accountsDto) {
        accountsDto.setAccountNumber(accounts.getAccountNumber());
        accountsDto.setAccountType(accounts.getAccountType());
        accountsDto.setBranchAddress(accounts.getBranchAddress());
        return accountsDto;
    }

    public static Accounts mapToAccounts(AccountsDto accountsDto, Accounts accounts) {
        accounts.setAccountNumber(accountsDto.getAccountNumber());
        accounts.setAccountType(accountsDto.getAccountType());
        accounts.setBranchAddress(accountsDto.getBranchAddress());
        return accounts;
    }

}
