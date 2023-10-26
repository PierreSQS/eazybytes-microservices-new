package com.eazybytes.cards.controller;

import com.eazybytes.cards.service.ICardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CardsControllerTest {

    @MockBean
    ICardsService iCardsServ;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createCardWithMobileNumber() throws Exception {
        mockMvc.perform(post("/api/create").param("mobileNumber","0123456789"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMsg").value("Card created successfully"))
                .andDo(print());

        verify(iCardsServ).createCard(anyString());
    }
    @Test
    void createCardNoMobileNumber() throws Exception {
        mockMvc.perform(post("/api/create"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Required parameter 'mobileNumber' is not present."))
                .andDo(print());

        verifyNoInteractions(iCardsServ);
    }

    @Test
    void fetchCardDetails() {
    }

    @Test
    void updateCardDetails() {
    }

    @Test
    void deleteCardDetails() {
    }
}