package net.provera.productserv.service.impl;

import lombok.RequiredArgsConstructor;
import net.provera.productserv.config.RabbitMQConfig;
import net.provera.productserv.dao.ProductRepository;
import net.provera.productserv.dao.model.Product;
import net.provera.productserv.dto.ProductDTO;
import net.provera.productserv.event.ProductEvent;
import net.provera.productserv.event.ProductEventType;
import net.provera.productserv.exception.ResourceNotFoundException;
import net.provera.productserv.mapper.ProductMapper;
import net.provera.productserv.service.ProductService;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ProductServiceImpl implements ProductService {
    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Product> redisTemplate;

    @Override
    public ProductDTO createProduct(ProductDTO productDto) {
        Product product = productMapper.toProduct(productDto);
        Product createdProduct = productRepository.save(product);
        ProductDTO createdProductDto = productMapper.toProductDTO(createdProduct);

        // Publish product created event to RabbitMQ
        ProductEvent event = new ProductEvent(createdProduct.getId(), ProductEventType.PRODUCT_CREATED,
                createdProduct.getName(), createdProduct.getDescription(), createdProduct.getPrice(),
                createdProduct.getQuantity(), createdProduct.getCategoryId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, event);

        return createdProductDto;
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO productDto) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        Product productToUpdate = optionalProduct.get();
        productToUpdate.setName(productDto.getName());
        productToUpdate.setDescription(productDto.getDescription());
        productToUpdate.setPrice(productDto.getPrice());
        productToUpdate.setQuantity(productDto.getQuantity());
        productToUpdate.setCategoryId(productDto.getCategoryId());

        Product updatedProduct = productRepository.save(productToUpdate);
        ProductDTO updatedProductDto = productMapper.toProductDTO(updatedProduct);

        // Publish product updated event to RabbitMQ
        ProductEvent event = new ProductEvent(updatedProduct.getId(), ProductEventType.PRODUCT_UPDATED,
                updatedProduct.getName(), updatedProduct.getDescription(), updatedProduct.getPrice(),
                updatedProduct.getQuantity(), updatedProduct.getCategoryId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, event);

        return updatedProductDto;
    }

    @Override
    public void deleteProduct(String id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);

        // Publish product deleted event to RabbitMQ
        ProductEvent event = new ProductEvent(id, ProductEventType.PRODUCT_DELETED,
                optionalProduct.get().getName(),
                optionalProduct.get().getDescription(),
                optionalProduct.get().getPrice(),
                optionalProduct.get().getQuantity(),
                optionalProduct.get().getCategoryId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, event);
    }


    @Override
    public ProductDTO getProductById(String id) {
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        Product cachedProduct = redisTemplate.opsForValue().get(cacheKey);
        if (Objects.nonNull(cachedProduct)) {
            return productMapper.toProductDTOFromCache(cachedProduct);
        }
        Optional<Product> product = productRepository.findById(id);
        ProductDTO productDto = product
                .map(productMapper::toProductDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        redisTemplate.opsForValue().set(cacheKey, product.get(), 1, TimeUnit.HOURS);
        return productMapper.toProductDTO(product.get());
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toProductDTO)
                .collect(Collectors.toList());
    }
}
