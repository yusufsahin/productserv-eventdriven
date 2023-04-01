package net.provera.productserv.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {
    private String id;
    private ProductEventType eventType;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String categoryId;
}