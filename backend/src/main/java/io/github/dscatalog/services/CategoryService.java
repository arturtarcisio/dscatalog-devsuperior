package io.github.dscatalog.services;

import io.github.dscatalog.entities.Category;
import io.github.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public List<Category> findAll () {
        return repository.findAll();
    }
}
