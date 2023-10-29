package com.eazybytes.loans.controller;

import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.exception.ResourceNotFoundException;
import com.eazybytes.loans.service.ILoansService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoansControllerTest {

    public static final String API_CREATE_URL = "/api/create";
    public static final String UPDATE_API_URL = "/api/update";
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
    void createLoan_MobileNr_OK() throws Exception {
        mockMvc.perform(post(API_CREATE_URL)
                        .param("mobileNumber",loansDto.getMobileNumber()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMsg")
                        .value(LoansConstants.MESSAGE_201))
                .andDo(print());

        verify(iLoansServMock).createLoan(loansDto.getMobileNumber());
    }

    @Test
    void createLoan_MobileNr_Not_Valid() throws Exception {
        // Given
        loansDto.setMobileNumber("111222333");

        mockMvc.perform(post(API_CREATE_URL)
                        .param("mobileNumber",loansDto.getMobileNumber()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value(containsString("createLoan.mobileNumber")))
                .andDo(print());
    }

    @Test
    void fetchLoanDetails_MobileNr_OK()  throws Exception {
        // Given
        given(iLoansServMock.fetchLoan(anyString())).willReturn(loansDto);

        mockMvc.perform(get("/api/fetch")
                        .param("mobileNumber",loansDto.getMobileNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanNumber")
                        .value(loansDto.getLoanNumber()))
                .andDo(print());
    }

    @Test
    void fetchLoanDetails_MobileNr_Invalid()  throws Exception {
        // Given
        ResourceNotFoundException rnfe = new ResourceNotFoundException("Loan", "mobileNumber",
                loansDto.getMobileNumber());
        doThrow(rnfe).when(iLoansServMock).fetchLoan(loansDto.getMobileNumber());

        mockMvc.perform(get("/api/fetch")
                        .param("mobileNumber",loansDto.getMobileNumber()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage")
                        .value(containsString("Loan not found")))
                .andDo(print());
    }

    @Test
    void updateLoanDetails_Updated() throws Exception {
        // Given
        given(iLoansServMock.updateLoan(any(LoansDto.class))).willReturn(Boolean.TRUE);
        mockMvc.perform(put(UPDATE_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loansDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value(LoansConstants.MESSAGE_200))
                .andDo(print());
    }

    @Test
    void updateLoanDetails_InvalidBody() throws Exception {
        // Given
        ConstraintViolationException cvex =
                new ConstraintViolationException("updateLoanDetails.mobileNumber",new HashSet<>());
        doThrow(cvex).when(iLoansServMock).updateLoan(loansDto);

        mockMvc.perform(put(UPDATE_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loansDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void updateLoanDetails_NotFound() throws Exception {
        // Given
        given(iLoansServMock.updateLoan(any(LoansDto.class))).willReturn(Boolean.FALSE);
        mockMvc.perform(put(UPDATE_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loansDto)))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.statusMsg").value(LoansConstants.MESSAGE_417_UPDATE))
                .andDo(print());
    }

    @Test
    void deleteLoanDetails() {
    }
}