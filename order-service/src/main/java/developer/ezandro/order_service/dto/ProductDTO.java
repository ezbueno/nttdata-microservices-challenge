package developer.ezandro.order_service.dto;

public record ProductDTO(
        Long id,
        String name,
        String description,
        double price
) {}