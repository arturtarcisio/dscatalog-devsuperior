package io.github.dscatalog.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.dscatalog.entities.Product;
import io.github.dscatalog.repositories.ProductRepository;
import io.github.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
public class ProductServiceIntegrationTests {

	private final ProductService service;	
	private final ProductRepository prodRepository;
	
	public ProductServiceIntegrationTests(ProductService service, ProductRepository prodRepository) {
		this.service = service;
		this.prodRepository = prodRepository;
	}
	
	private Long existentId;
	private Long nonExistentId;
	private Long countTotalProducts;


	@BeforeEach
	void setup() throws Exception {		
		existentId = 1L;
		nonExistentId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		service.delete(existentId);
		Assertions.assertEquals(countTotalProducts - 1, prodRepository.count());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistentId);
		});
		
	}
	
	
	
	
	
}
