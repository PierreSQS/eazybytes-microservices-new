package com.eazybytes.cards.controller;

import com.eazybytes.cards.constants.CardsConstants;
import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.exception.ResourceNotFoundException;
import com.eazybytes.cards.service.ICardsService;
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

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CardsControllerTest {

    public static final String UPDATE_API_URL = "/api/update";
    public static final String CREATE_API_URL = "/api/create";
    public static final String DELETE_API_URL = "/api/delete";
    @MockBean
    ICardsService iCardsServMock;

    @Autowired
    ObjectMapper objectMapper;

    CardsDto cardsDto;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        cardsDto = new CardsDto();
        cardsDto.setCardNumber("100646930341");
        cardsDto.setCardType(CardsConstants.CREDIT_CARD);
        cardsDto.setMobileNumber("2222222222");
        cardsDto.setTotalLimit(2500);
        cardsDto.setAmountUsed(1000);
        cardsDto.setAvailableAmount(1500);
    }

    @Test
    void createCardWithMobileNumber() throws Exception {
        mockMvc.perform(post(CREATE_API_URL).param("mobileNumber","0123456789"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMsg").value("Card created successfully"))
                .andDo(print());

        verify(iCardsServMock).createCard(anyString());
    }
    @Test
    void createCardNoMobileNumber() throws Exception {
        mockMvc.perform(post(CREATE_API_URL))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'mobileNumber' is not present."))
                .andDo(print());

        verifyNoInteractions(iCardsServMock);
    }

    @Test
    void fetchCardDetails_CardNr_OK() throws Exception {
        // Given
        given(iCardsServMock.fetchCard(cardsDto.getMobileNumber())).willReturn(cardsDto);

        mockMvc.perform(get("/api/fetch")
                        .param("mobileNumber",cardsDto.getMobileNumber())
                        .header("eazybank-correlation-id",UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobileNumber").value(cardsDto.getMobileNumber()))
                .andDo(print());
    }

    @Test
    void fetchCardDetails_CardNr_NOT_VALID() throws Exception {
        // Given

        ConstraintViolationException constViolException =
                new ConstraintViolationException("Mobile number must be 10 digits", null);
        cardsDto.setMobileNumber("131313ac");
        doThrow(constViolException).when(iCardsServMock).fetchCard(cardsDto.getMobileNumber());

        mockMvc.perform(get("/api/fetch")
                        .param("mobileNumber",cardsDto.getMobileNumber())
                        .header("eazybank-correlation-id",UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(containsString("fetchCardDetails.mobileNumber")))
                .andDo(print());
    }

    @Test
    void updateCardDetailsUpdated() throws Exception {
        // Given
        given(iCardsServMock.updateCard(any(CardsDto.class))).willReturn(Boolean.TRUE);

        mockMvc.perform(put(UPDATE_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardsDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value(CardsConstants.MESSAGE_200))
                .andDo(print());
    }

    @Test
    void updateCardDetailsNOTUpdated() throws Exception {
        // Given
        given(iCardsServMock.updateCard(any(CardsDto.class))).willReturn(Boolean.FALSE);

        mockMvc.perform(put(UPDATE_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardsDto)))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.statusMsg").value(CardsConstants.MESSAGE_417_UPDATE))
                .andDo(print());
    }

    @Test
    void updateCardDetailsCardNotFound() throws Exception {
        // Given
        ResourceNotFoundException resNFE = new ResourceNotFoundException("Card","CardNumber",cardsDto.getCardNumber());
        doThrow(resNFE).when(iCardsServMock).updateCard(cardsDto);

        mockMvc.perform(put(UPDATE_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardsDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(containsString(cardsDto.getCardNumber())))
                .andDo(print());
    }


    @Test
    void deleteCardDetailsCardFound() throws Exception {
        // Given
        given(iCardsServMock.updateCard(any(CardsDto.class))).willReturn(Boolean.TRUE);

        mockMvc.perform(delete(DELETE_API_URL)
                        .param("mobileNumber",cardsDto.getMobileNumber()))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.statusMsg").value(CardsConstants.MESSAGE_417_DELETE))
                .andDo(print());
    }

    @Test
    void deleteCardDetailsCardNotFound() throws Exception {
        // Given
        ResourceNotFoundException resNFE = new ResourceNotFoundException("Card","CardNumber",cardsDto.getCardNumber());
        doThrow(resNFE).when(iCardsServMock).deleteCard(cardsDto.getMobileNumber());

        mockMvc.perform(delete(DELETE_API_URL)
                        .param("mobileNumber",cardsDto.getMobileNumber()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(containsString(cardsDto.getCardNumber())))
                .andDo(print());
    }
}