package io.github.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import io.github.dscatalog.entities.Product;
import io.github.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistentId;
	private Long countTotalProduct;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistentId = 900L;
		countTotalProduct = 25L;
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {		
		repository.deleteById(existingId);		
		Optional<Product> result = repository.findById(existingId);		
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		var product = Factory.createProduct(); 
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProduct + 1, product.getId());
		
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistentId);
		});
	}

}
