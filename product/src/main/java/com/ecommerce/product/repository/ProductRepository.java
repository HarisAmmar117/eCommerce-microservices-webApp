package com.ecommerce.product.repository;


import aj.org.objectweb.asm.commons.Remapper;
import com.ecommerce.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByActiveTrue();

    @Query("Select p from products p where p.active=true AND p.quantity>0 AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    Optional<Product> findByIdAndActiveTrue(Long id);
}
