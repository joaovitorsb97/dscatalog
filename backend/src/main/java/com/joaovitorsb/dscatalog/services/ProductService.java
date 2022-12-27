package com.joaovitorsb.dscatalog.services;

import com.joaovitorsb.dscatalog.dtos.CategoryDTO;
import com.joaovitorsb.dscatalog.dtos.ProductDTO;
import com.joaovitorsb.dscatalog.entities.Category;
import com.joaovitorsb.dscatalog.entities.Product;
import com.joaovitorsb.dscatalog.repositories.CategoryRepository;
import com.joaovitorsb.dscatalog.repositories.ProductRepository;
import com.joaovitorsb.dscatalog.services.exceptions.DatabaseException;
import com.joaovitorsb.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.lang.module.ResolutionException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable){
        Page<Product> page = productRepository.findAll(pageable);
        return page.map(x -> new ProductDTO(x, x.getCategories()));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Optional<Product> obj = productRepository.findById(id);
        return obj.stream().map(x -> new ProductDTO(x, x.getCategories())).findFirst().orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    public ProductDTO insert(ProductDTO productDTO){
        Product product = new Product();
        copyDtoToEntity(productDTO, product);
        return new ProductDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO){
        try{
            Product product = productRepository.getById(id);
            copyDtoToEntity(productDTO, product);
            return new ProductDTO(productRepository.save(product));
        }catch(EntityNotFoundException e){
            throw new ResourceNotFoundException("Id " + id + " not found");
        }
    }

    @Transactional
    public void delete(Long id){
        try{
            productRepository.deleteById(id);
        }catch(EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id " + id + " not found");
        }catch(DataIntegrityViolationException e){
            throw new DatabaseException(e.getMessage());
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity){
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());

        entity.getCategories().clear();

        for(CategoryDTO categoryDTO : dto.getCategories()){
            Category category = categoryRepository.getById(categoryDTO.getId());
            entity.getCategories().add(category);
        }
    }
}
