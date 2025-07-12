package developer.ezandro.order_service.dto;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
    public OrderItemResponseDTO(Long id, Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
        this(id, productId, productName, quantity, unitPrice, unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }
}