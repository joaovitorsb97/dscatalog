package com.joaovitorsb.dscatalog.services;

import com.joaovitorsb.dscatalog.dtos.ProductDTO;
import com.joaovitorsb.dscatalog.entities.Category;
import com.joaovitorsb.dscatalog.entities.Product;
import com.joaovitorsb.dscatalog.entities.factory.Factory;
import com.joaovitorsb.dscatalog.repositories.CategoryRepository;
import com.joaovitorsb.dscatalog.repositories.ProductRepository;
import com.joaovitorsb.dscatalog.services.exceptions.DatabaseException;
import com.joaovitorsb.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO productDTO;
    private Category category;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 35L;
        dependentId = 4L;
        product = Factory.createProduct();
        productDTO = Factory.productDTO();
        category = Factory.createCategory();
        page = new PageImpl<>(List.of(product));

        //FindAll behavior simulated
        Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        //FindById behavior simulated
        Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //UPDATE behavior simulated
        Mockito.when(productRepository.getById(existingId)).thenReturn(product);
        Mockito.when(productRepository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(categoryRepository.getById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        //INSERT behavior simulated
        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        //DELETE behavior simulated
        Mockito.doNothing().when(productRepository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
    }

    @Test
    void findAllPagedShouldReturnPageable(){
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ProductDTO> result = productService.findAllPaged(pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.hasContent());
        Mockito.verify(productRepository, Mockito.times(1)).findAll(pageRequest);
    }

    @Test
    void updateShouldUpdateWhenIdExists(){
        ProductDTO objDTO = productService.update(existingId, productDTO);
        Assertions.assertNotNull(objDTO);
    }

    @Test
    void updateShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           ProductDTO objDTO = productService.update(nonExistingId, productDTO);
        });
    }
    @Test
    void findByIdShouldReturnProductDTOWhenIdExists(){
        ProductDTO objDTO = productService.findById(existingId);
        Assertions.assertNotNull(objDTO);
    }

    @Test
    void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           ProductDTO objDTO = productService.findById(nonExistingId);
        });
    }

    @Test
    void deleteByIdShouldThrowsDatabaseExceptionWhenIdIsDependent(){
        Assertions.assertThrows(DatabaseException.class, () -> {
           productService.delete(dependentId);
        });
    }
    @Test
    void deleteByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () ->{
           productService.delete(nonExistingId);
        });
    }
    @Test
    void deleteByIdShouldDoNothingWhenIdExists(){
        Assertions.assertDoesNotThrow(() -> {
            productService.delete(existingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);
    }
}
