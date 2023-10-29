package com.eazybytes.loans.controller;

import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.service.ILoansService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoansControllerTest {

    public static final String API_CREATE_URL = "/api/create";
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ILoansService iLoansServMock;

    @Autowired
    MockMvc mockMvc;

    LoansDto loansDto;

    @BeforeEach
    void setUp() {
        loansDto = new LoansDto();
        loansDto.setLoanType(LoansConstants.HOME_LOAN);
        loansDto.setLoanNumber("111111111111");
        loansDto.setMobileNumber("3333333333");
        loansDto.setTotalLoan(5000);
        loansDto.setAmountPaid(5000);
        loansDto.setOutstandingAmount(3500);
    }

    @Test
    void createLoan_Mobile_OK() throws Exception {
        mockMvc.perform(post(API_CREATE_URL)
                        .param("mobileNumber",loansDto.getMobileNumber()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMsg")
                        .value(LoansConstants.MESSAGE_201))
                .andDo(print());

        verify(iLoansServMock).createLoan(loansDto.getMobileNumber());
    }

    @Test
    void fetchLoanDetails() {
    }

    @Test
    void updateLoanDetails() {
    }

    @Test
    void deleteLoanDetails() {
    }
}