package com.ecommerce.product.service;


import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductResponse createProduct(ProductRequest productRequest){

        Product product = new Product();
        updateProductFromRequest(product,productRequest);

        Product savedProduct = repository.save(product);
        return mapToProductResponse(savedProduct);

    }

    public Optional<ProductResponse> editProduct(Long id, ProductRequest productRequest){

        return repository.findById(id)
                .map(existingProduct ->{

                    updateProductFromRequest(existingProduct,productRequest);
                    Product savedProduct = repository.save(existingProduct);
                    return mapToProductResponse(savedProduct);
                });
    }

    public List<ProductResponse> fetchAllProducts() {

        return repository.findByActiveTrue().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

    }

    public Optional<ProductResponse> fetchProduct(Long id){

        return repository.findByIdAndActiveTrue(id)
                .map(this::mapToProductResponse);
    }

    public boolean deleteProduct(Long id) {

        return repository.findById(id)
                .map(product ->
                        {
                            product.setActive(false);
                            repository.save(product);
                            return true;
                        }

                        ).orElse(false);
    }

    private ProductResponse mapToProductResponse(Product product) {

        ProductResponse response = new ProductResponse();
        response.setId(String.valueOf(product.getId()));
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setCategory(product.getCategory());
        response.setImageURL(product.getImageURL());
        response.setActive(product.getActive());

        return response;

    }

    public void updateProductFromRequest(Product product,ProductRequest request){

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());
        product.setImageURL(request.getImageURL());
    }


    public List<ProductResponse> searchProducts(String keyword) {

        return repository.searchProducts(keyword).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }
}
