package net.provera.productserv.dao;

import net.provera.productserv.dao.model.Product;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProductRedisRepository {
    private static final String KEY = "product:";
    private final RedisTemplate<String, Product> redisTemplate;
    private final HashOperations<String, String, Product> hashOperations;

    public ProductRedisRepository(RedisTemplate<String, Product> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void save(Product product) {
        hashOperations.put(KEY, product.getId(), product);
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(hashOperations.get(KEY, id));
    }

    public void deleteById(String id) {
        hashOperations.delete(KEY, id);
    }

}
