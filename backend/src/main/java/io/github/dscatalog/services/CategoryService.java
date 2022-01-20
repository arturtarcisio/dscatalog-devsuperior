package io.github.dscatalog.services;

import io.github.dscatalog.dto.CategoryDTO;
import io.github.dscatalog.entities.Category;
import io.github.dscatalog.repositories.CategoryRepository;
import io.github.dscatalog.services.exceptions.AttributeNullOrEmptyException;
import io.github.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll () {
        List<Category> list = repository.findAll();
        //List<CategoryDTO> listDTO = list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
        return list.stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> optionalCategory = repository.findById(id);
        Category entity = optionalCategory.orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO categoryDTO) {

        if (categoryDTO.getName().isEmpty() || categoryDTO.getName() == null) {
            throw new AttributeNullOrEmptyException("Every attribute of object must be filled.");
        }

        Category category = Category.builder()
                .name(categoryDTO.getName())
                .build();

        Category savedCategory = repository.save(category);
        return new CategoryDTO(savedCategory);
    }
}
