package net.provera.productserv.dao;

import net.provera.productserv.dao.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product,String> {
    List<Product> findByCategoryId(String categoryId);
}
