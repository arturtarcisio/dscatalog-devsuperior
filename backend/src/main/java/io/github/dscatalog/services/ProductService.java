package io.github.dscatalog.services;

import io.github.dscatalog.dto.ProductDTO;
import io.github.dscatalog.entities.Product;
import io.github.dscatalog.repositories.ProductRepository;
import io.github.dscatalog.services.exceptions.AttributeNullOrEmptyException;
import io.github.dscatalog.services.exceptions.DataBaseException;
import io.github.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged (PageRequest pageRequest) {
        Page<Product> list = repository.findAll(pageRequest);
        return list.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> optionalProduct = repository.findById(id);
        Product entity = optionalProduct.orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO productDTO) {

        if (productDTO.getName().isEmpty() || productDTO.getName() == null) {
            throw new AttributeNullOrEmptyException("Every attribute of object must be filled.");
        }

        Product product = Product.builder()
                //.name(productDTO.getName())
                .build();

        Product savedProduct = repository.save(product);
        return new ProductDTO(savedProduct);
    }

    @Transactional
    public ProductDTO update(ProductDTO productDTO, Long id) {

        if (productDTO.getName().isEmpty() || productDTO.getName() == null) {
            throw new AttributeNullOrEmptyException("Every attribute of object must be filled.");
        }
        try {
            Product entity = repository.getOne(id);
            entity.setName(productDTO.getName());
            entity = repository.save(entity);
            return new ProductDTO(entity);
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
