package com.joaovitorsb.dscatalog.services;

import com.joaovitorsb.dscatalog.dtos.CategoryDTO;
import com.joaovitorsb.dscatalog.entities.Category;
import com.joaovitorsb.dscatalog.repositories.CategoryRepository;
import com.joaovitorsb.dscatalog.services.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
       List<Category> list = categoryRepository.findAll();
       List<CategoryDTO> listDto = list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
       return new PageImpl<>(listDto);
    }
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Optional<Category> obj = categoryRepository.findById(id);
        Optional<CategoryDTO> objDTO = obj.stream().map(x -> new CategoryDTO(x)).findFirst();
        return objDTO.orElseThrow(() -> new EntityNotFoundException(id));
    }
    @Transactional
    public CategoryDTO insert(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return new CategoryDTO(categoryRepository.save(category));
    }
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.getById(id);
        updateData(categoryDTO, category);
        return new CategoryDTO(categoryRepository.save(category));

    }
    public void updateData(CategoryDTO categoryDTO, Category category){
        category.setName(categoryDTO.getName());
    }
}
