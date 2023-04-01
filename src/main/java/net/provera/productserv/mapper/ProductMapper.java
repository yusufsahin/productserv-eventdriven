package net.provera.productserv.mapper;
import net.provera.productserv.dao.model.Product;
import net.provera.productserv.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toProductDTO(Product product);
    Product toProduct(ProductDTO productDTO);
    default ProductDTO toProductDTOFromCache(Product product) {
        return toProductDTO(product);
    }

}