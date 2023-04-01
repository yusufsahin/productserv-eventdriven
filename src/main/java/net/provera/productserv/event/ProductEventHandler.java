package net.provera.productserv.event;
import lombok.RequiredArgsConstructor;
import net.provera.productserv.config.RabbitMQConfig;
import net.provera.productserv.dao.ProductRepository;
import net.provera.productserv.dao.model.Product;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductEventHandler {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Product> redisTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleProductEvents(ProductEvent event) {
        Optional<Product> optionalProduct = productRepository.findById(event.getId());
        switch (event.getEventType()) {
            case PRODUCT_CREATED:
                if (!optionalProduct.isPresent()) {
                    Product product = new Product();
                    product.setId(event.getId());
                    product.setName(event.getName());
                    product.setDescription(event.getDescription());
                    product.setPrice(event.getPrice());
                    product.setQuantity(event.getQuantity());
                    product.setCategoryId(event.getCategoryId());
                    productRepository.save(product);
                    redisTemplate.opsForValue().set(product.getId(), product);
                }
                break;
            case PRODUCT_UPDATED:
                if (optionalProduct.isPresent()) {
                    Product productToUpdate = optionalProduct.get();
                    productToUpdate.setName(event.getName());
                    productToUpdate.setDescription(event.getDescription());
                    productToUpdate.setPrice(event.getPrice());
                    productToUpdate.setQuantity(event.getQuantity());
                    productToUpdate.setCategoryId(event.getCategoryId());
                    productRepository.save(productToUpdate);
                    redisTemplate.opsForValue().set(productToUpdate.getId(), productToUpdate);
                }
                break;
            case PRODUCT_DELETED:
                if (optionalProduct.isPresent()) {
                    productRepository.deleteById(event.getId());
                    redisTemplate.delete(event.getId());
                }
                break;
            default:
                break;
        }
    }
}