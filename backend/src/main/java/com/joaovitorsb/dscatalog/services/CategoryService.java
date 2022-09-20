package com.joaovitorsb.dscatalog.services;

import com.joaovitorsb.dscatalog.entities.Category;
import com.joaovitorsb.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        list.add(new Category(null, "Books"));
        list.add(new Category(null, "Games"));
        return list;
    }
}
