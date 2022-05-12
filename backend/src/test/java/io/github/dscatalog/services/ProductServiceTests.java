package io.github.dscatalog.services;

import java.util.List;
import java.util.Optional;

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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.dscatalog.tests.Factory;

import io.github.dscatalog.entities.Product;
import io.github.dscatalog.repositories.ProductRepository;
import io.github.dscatalog.services.exceptions.DataBaseException;
import io.github.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	private long existingId;
	private long nonExistentId;
	private long idWithDataIntegrityViolation;
	private PageImpl<Product> page;
	private Product product;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistentId = 900L;
		idWithDataIntegrityViolation = 4L;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		
		// Simulando delete by id
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistentId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idWithDataIntegrityViolation);
		
		// Simulando o find all pageable
		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		
		// Simulando o save
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		// Simulando o find by id
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
	}
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistentId);
		});		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistentId);
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenIdWithDataIntegrityViolationExist() {
		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(idWithDataIntegrityViolation);
		});		
		Mockito.verify(repository, Mockito.times(1)).deleteById(idWithDataIntegrityViolation);
	}

}
