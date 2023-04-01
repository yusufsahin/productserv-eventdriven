package net.provera.productserv.service;

import net.provera.productserv.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    ProductDTO getProductById(String id);

    List<ProductDTO> getAllProducts();

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(String id, ProductDTO productDTO);

    void deleteProduct(String id);
    List<ProductDTO> getProductsByCategoryId(String categoryId); // Add this line

}
