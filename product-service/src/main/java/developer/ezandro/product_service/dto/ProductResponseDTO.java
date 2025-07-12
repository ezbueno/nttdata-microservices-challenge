package developer.ezandro.product_service.dto;

import developer.ezandro.product_service.model.Product;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        double price) {

    public ProductResponseDTO(Product product) {
        this(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }
}