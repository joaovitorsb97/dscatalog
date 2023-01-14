package com.joaovitorsb.dscatalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaovitorsb.dscatalog.dtos.ProductDTO;
import com.joaovitorsb.dscatalog.entities.Product;
import com.joaovitorsb.dscatalog.entities.factory.Factory;
import com.joaovitorsb.dscatalog.services.ProductService;
import com.joaovitorsb.dscatalog.services.exceptions.DatabaseException;
import com.joaovitorsb.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; //Object necessary to convert Java to Json

    private ProductDTO productDTO;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private PageImpl<ProductDTO> page;

    @BeforeEach
    void setUp() throws Exception{
        productDTO = Factory.productDTO();
        page = new PageImpl<>(List.of(productDTO));
        existingId = 1L;
        nonExistingId = 26L;

        //FIND ALL SERVICE BEHAVIOR SIMULATED
        Mockito.when(productService.findAllPaged(any())).thenReturn(page);

        //FIND BY ID SERVICE BEHAVIOR SIMULATED
        Mockito.when(productService.findById(existingId)).thenReturn(productDTO);
        Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        //UPDATE SERVICE BEHAVIOR SIMULATED
        Mockito.when(productService.update(eq(existingId), any())).thenReturn(productDTO);
        Mockito.when(productService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        //INSERT SERVICE BEHAVIOR SIMULATED
        Mockito.when(productService.insert(any())).thenReturn(productDTO);

        //DELETE SERVICE BEHAVIOR SIMULATED
        Mockito.doNothing().when(productService).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(productService).delete(dependentId);
    }

    @Test
    public void deleteShouldDeleteProductWhenIdExists() throws Exception{
        ResultActions resultActions = mockMvc.perform(delete("/products/{id}", existingId));

        resultActions.andExpect(status().isNoContent());
    }
    @Test
    public void deleteShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception{
        ResultActions resultActions = mockMvc.perform(delete("/products/{id}", nonExistingId));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldThrowsDatabaseExceptionWhenIdIsDependent() throws Exception{
        ResultActions resultActions = mockMvc.perform(delete("/products/{id}", dependentId));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void insertShouldInsertProductDTOWhenIdIsNull() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions resultActions = mockMvc.perform(post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findAllPagedShouldReturnProductsPaged() throws Exception{
        PageRequest pageRequest = PageRequest.of(0, 20);

        ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());

        Mockito.verify(productService, Mockito.times(1)).findAllPaged(pageRequest);
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception{
        ResultActions resultActions = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.description").exists());

    }
    @Test
    public void findByIdShouldReturnStatusNotFoundWhenIdDoesNotExists() throws Exception{
        ResultActions resultActions = mockMvc.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldUpdateProductDTOWhenIdExists() throws Exception{

        String jsonBody = objectMapper.writeValueAsString(productDTO); //Writting productDTO as Json format

        ResultActions resultActions = mockMvc.perform(put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void updateShouldReturnStatusNotFoundWhenIdDoesNotExists() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(put("/products/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }
}
