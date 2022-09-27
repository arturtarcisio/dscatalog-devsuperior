package io.github.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.dscatalog.dto.CategoryDTO;
import io.github.dscatalog.entities.Category;
import io.github.dscatalog.repositories.CategoryRepository;
import io.github.dscatalog.services.exceptions.AttributeNullOrEmptyException;
import io.github.dscatalog.services.exceptions.DataBaseException;
import io.github.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged (Pageable pageable) {
        Page<Category> list = repository.findAll(pageable);
        return list.map(CategoryDTO::new);
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

        Category category = new Category();
        category.setName(categoryDTO.getName());

        Category savedCategory = repository.save(category);
        return new CategoryDTO(savedCategory);
    }

    @Transactional
    public CategoryDTO update(CategoryDTO categoryDTO, Long id) {

        if (categoryDTO.getName().isEmpty() || categoryDTO.getName() == null) {
            throw new AttributeNullOrEmptyException("Every attribute of object must be filled.");
        }
        try {
            Category entity = repository.getOne(id);
            entity.setName(categoryDTO.getName());
            entity = repository.save(entity);
            return new CategoryDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID invalid. Resource not found!");
        }
    }

    @Transactional
    public void delete(Long id) {
        try{
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("ID invalid. Resource not found!");
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation!");
        }
    }
}
