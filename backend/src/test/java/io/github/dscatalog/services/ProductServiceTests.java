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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.dscatalog.dto.ProductDTO;
import io.github.dscatalog.entities.Product;
import io.github.dscatalog.repositories.CategoryRepository;
import io.github.dscatalog.repositories.ProductRepository;
import io.github.dscatalog.services.exceptions.DataBaseException;
import io.github.dscatalog.services.exceptions.ResourceNotFoundException;
import io.github.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	private long existingId;
	private long nonExistentId;
	private long idWithDataIntegrityViolation;
	private PageImpl<Product> page;
	private Product product;
	private ProductDTO prodDTO;
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository prodRepository;
	
	@Mock
	private CategoryRepository catRepository;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistentId = 900L;
		idWithDataIntegrityViolation = 4L;
		product = Factory.createProduct();
		prodDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(product));		
		
		// Simulando o find all pageable
		Mockito.when(prodRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);		
		
		// Simulando o save
		Mockito.when(prodRepository.save(ArgumentMatchers.any())).thenReturn(product);
		
		// Simulando o find by id
		Mockito.when(prodRepository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(prodRepository.findById(nonExistentId)).thenReturn(Optional.empty());
		
		// Simulando update
		Mockito.when(prodRepository.getOne(existingId)).thenReturn(product);
		Mockito.when(prodRepository.getOne(nonExistentId)).thenThrow(ResourceNotFoundException.class);
		
		// Simulando delete by id
		Mockito.doNothing().when(prodRepository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(prodRepository).deleteById(nonExistentId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(prodRepository).deleteById(idWithDataIntegrityViolation);
	}	
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {		
		var result = service.update(existingId, prodDTO);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistentId, prodDTO);
		});		
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		var result = service.findById(existingId);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistentId);
		});		
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable page = PageRequest.of(0, 1);
		Page<ProductDTO> result = service.findAllPaged(page);

		Assertions.assertNotNull(result);
		Mockito.verify(prodRepository).findAll(page);
	}
		
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});		
		Mockito.verify(prodRepository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistentId);
		});		
		Mockito.verify(prodRepository, Mockito.times(1)).deleteById(nonExistentId);
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenIdWithDataIntegrityViolationExist() {
		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(idWithDataIntegrityViolation);
		});		
		Mockito.verify(prodRepository, Mockito.times(1)).deleteById(idWithDataIntegrityViolation);
	}

}
