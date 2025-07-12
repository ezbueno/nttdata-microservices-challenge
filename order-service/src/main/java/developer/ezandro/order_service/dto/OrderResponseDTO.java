package developer.ezandro.order_service.dto;

import developer.ezandro.order_service.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        OrderStatus status,
        List<OrderItemResponseDTO> items
) {}