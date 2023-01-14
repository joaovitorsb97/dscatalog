package com.joaovitorsb.dscatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaovitorsb.dscatalog.dtos.ProductDTO;
import com.joaovitorsb.dscatalog.entities.factory.Factory;
import com.joaovitorsb.dscatalog.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long countingTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 35L;
        countingTotalProducts = 25L;
    }

    @Test
    void findAllShouldReturnAllProductsSortedByNameWhenSortByName() throws Exception{
        ResultActions resultActions = mockMvc.perform(get("/products?page=0&size=12&sort=name,asc").accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.totalElements").value(countingTotalProducts));
        resultActions.andExpect(jsonPath("$.content").exists());
        resultActions.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        resultActions.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        resultActions.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    void updateShouldUpdateByIdProductWhenIdExists() throws Exception{
        ProductDTO productDTO = Factory.productDTO();

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(existingId));
        resultActions.andExpect(jsonPath("$.name").value(productDTO.getName()));
        resultActions.andExpect(jsonPath("$.description").value(productDTO.getDescription()));
    }

    @Test
    void updateShouldThrowsNotFoundWhenIdDoesNotExists() throws Exception{
        ProductDTO productDTO = Factory.productDTO();

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(put("/products/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }
}
