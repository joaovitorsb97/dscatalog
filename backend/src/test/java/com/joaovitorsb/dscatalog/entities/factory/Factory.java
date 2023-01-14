package com.joaovitorsb.dscatalog.entities.factory;

import com.joaovitorsb.dscatalog.dtos.CategoryDTO;
import com.joaovitorsb.dscatalog.dtos.ProductDTO;
import com.joaovitorsb.dscatalog.entities.Category;
import com.joaovitorsb.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct(){
        Product product = new Product(1L, "Iphone", "Good Phone", 2000.0, "https://img.com/img.png", Instant.parse("2020-07-13T12:50:07.12345Z"));
        product.getCategories().add(new Category(2L, "Eletrônicos"));
        return product;
    }

    public static ProductDTO productDTO(){
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }

    public static Category createCategory(){
        return new Category(1L, "Phones");
    }

    public static CategoryDTO categoryDTO(){
        Category category = createCategory();
        return new CategoryDTO(category);
    }
}
