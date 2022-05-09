package io.github.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.dscatalog.repositories.ProductRepository;
import io.github.dscatalog.services.exceptions.DataBaseException;
import io.github.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	private long existingId;
	private long nonExistentId;
	private long idWithDataIntegrityViolation;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistentId = 900L;
		idWithDataIntegrityViolation = 4L;
		
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistentId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idWithDataIntegrityViolation);
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
