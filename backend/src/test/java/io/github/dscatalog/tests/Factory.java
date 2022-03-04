package io.github.dscatalog.tests;

import java.time.Instant;

import io.github.dscatalog.dto.ProductDTO;
import io.github.dscatalog.entities.Category;
import io.github.dscatalog.entities.Product;

public class Factory {
	
	public static Product createProduct() {
		var product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.png", Instant.parse("2020-10-20T03:00:00Z"));
		product.getCategories().add(new Category(2L, "Eletronics"));
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		var product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}

}
