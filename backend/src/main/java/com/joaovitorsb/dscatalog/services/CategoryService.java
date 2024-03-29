package com.joaovitorsb.dscatalog.services;

import com.joaovitorsb.dscatalog.dtos.CategoryDTO;
import com.joaovitorsb.dscatalog.entities.Category;
import com.joaovitorsb.dscatalog.repositories.CategoryRepository;
import com.joaovitorsb.dscatalog.services.exceptions.DatabaseException;
import com.joaovitorsb.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(Pageable pageable) {
       Page<Category> page = categoryRepository.findAll(pageable);
       return page.map(CategoryDTO::new);
    }
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Optional<Category> obj = categoryRepository.findById(id);
        return obj.stream().map(CategoryDTO::new).findFirst().orElseThrow(() -> new ResourceNotFoundException(id));
    }
    @Transactional
    public CategoryDTO insert(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return new CategoryDTO(categoryRepository.save(category));
    }
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        try{
            Category category = categoryRepository.getById(id);
            category.setName(categoryDTO.getName());
            return new CategoryDTO(categoryRepository.save(category));
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id " + id + " not found");
        }
    }
    public void delete(Long id) {
        try{
            categoryRepository.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id " + id + " not found");
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException(e.getMessage());
        }
    }

}
