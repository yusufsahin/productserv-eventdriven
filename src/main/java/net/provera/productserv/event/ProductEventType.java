package net.provera.productserv.event;
import lombok.Getter;

@Getter
public enum ProductEventType {
    PRODUCT_CREATED("Product Created"),
    PRODUCT_UPDATED("Product Updated"),
    PRODUCT_DELETED("Product Deleted");

    private final String value;

    ProductEventType(String value) {
        this.value = value;
    }
}