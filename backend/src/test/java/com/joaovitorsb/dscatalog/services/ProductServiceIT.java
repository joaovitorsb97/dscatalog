package com.joaovitorsb.dscatalog.services;

import com.joaovitorsb.dscatalog.dtos.ProductDTO;
import com.joaovitorsb.dscatalog.repositories.ProductRepository;
import com.joaovitorsb.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long totalProductsExisting;
    private PageRequest pageRequest;
    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 35L;
        totalProductsExisting = 25L;

    }

    @Test
    void deleteShouldDeleteProductWhenIdExists(){
        productService.delete(existingId);
        Assertions.assertEquals(totalProductsExisting - 1, productRepository.count());
    }

    @Test
    void deleteShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () ->{
            productService.delete(nonExistingId);
        });
        Assertions.assertEquals(totalProductsExisting, productRepository.count());
    }

    @Test
    void findAllPagedShouldReturnAllProductsPagedWhenPage0AndSize10(){
        pageRequest = PageRequest.of(0, 10);
        Page<ProductDTO> page = productService.findAllPaged(pageRequest);

        Assertions.assertFalse(page.isEmpty());
        Assertions.assertTrue(page.hasContent());
        Assertions.assertEquals(0, page.getNumber());
        Assertions.assertEquals(10, page.getSize());
        Assertions.assertEquals(totalProductsExisting, page.getTotalElements());
    }

    @Test
    void findAllPagedShouldReturnEmptyWhenPageSizeDoesNotExists(){
        pageRequest = PageRequest.of(50, 10);
        Page<ProductDTO> page = productService.findAllPaged(pageRequest);

        Assertions.assertTrue(page.isEmpty());
    }

    @Test
    void findAllPagedShouldReturnSortedProductsWhenSortByName(){
        pageRequest = PageRequest.of(0, 10, Sort.by("name"));
        Page<ProductDTO> page = productService.findAllPaged(pageRequest);

        Assertions.assertEquals("Macbook Pro", page.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", page.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", page.getContent().get(2).getName());
    }
}

