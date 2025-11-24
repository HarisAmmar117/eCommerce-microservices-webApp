package com.ecom.app.service;

import com.ecom.app.dto.ProductRequest;
import com.ecom.app.dto.ProductResponse;
import com.ecom.app.model.Product;
import com.ecom.app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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


}
