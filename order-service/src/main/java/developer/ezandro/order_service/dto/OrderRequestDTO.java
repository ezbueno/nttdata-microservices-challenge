package developer.ezandro.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequestDTO(
        @Valid
        @NotEmpty(message = "Order must have at least one item")
        List<OrderItemRequestDTO> items
) {}