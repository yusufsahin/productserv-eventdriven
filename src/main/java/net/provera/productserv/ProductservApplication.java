package net.provera.productserv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
public class ProductservApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductservApplication.class, args);
    }

}
