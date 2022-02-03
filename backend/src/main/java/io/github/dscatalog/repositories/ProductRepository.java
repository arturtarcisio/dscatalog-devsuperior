package io.github.dscatalog.repositories;

import io.github.dscatalog.entities.Category;
import io.github.dscatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
