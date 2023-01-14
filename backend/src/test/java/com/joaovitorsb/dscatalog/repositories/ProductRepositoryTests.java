package com.joaovitorsb.dscatalog.repositories;

import com.joaovitorsb.dscatalog.entities.Product;
import com.joaovitorsb.dscatalog.entities.factory.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;
    private long existingId;
    private long nonExistingId;
    private long countingProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 35L;
        countingProducts = 26L;
    }

    @Test
    void saveShouldInsertNewProductWithAutoincrementWhenIdIsNull(){
        Product product = Factory.createProduct();
        product.setId(null);
        productRepository.save(product);
        Optional<Product> objById = productRepository.findById(countingProducts);
        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countingProducts, product.getId());
        Assertions.assertTrue(objById.isPresent());
    }

    @Test
    void findByIdShouldReturnProductWithTheSameIdWhenIdExists(){
       Optional<Product> objById = productRepository.findById(existingId);
       Assertions.assertTrue(objById.isPresent());
    }

    @Test
    void findByIdShouldReturnNullWhenIdDoesNotExists(){
        Optional<Product> objById = productRepository.findById(nonExistingId);
        Assertions.assertFalse(objById.isPresent());
    }

    @Test
    void deleteShouldDeleteProductWhenIdExists(){
        productRepository.deleteById(existingId);
        Optional<Product> objById = productRepository.findById(existingId);
        Assertions.assertFalse(objById.isPresent());
    }
    @Test
    void deleteShouldThrowsEmptyResultDataAccessExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(EmptyResultDataAccessException.class, () ->{
            productRepository.deleteById(nonExistingId);
        });
    }

}
