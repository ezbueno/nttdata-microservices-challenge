package developer.ezandro.order_service.service;

import developer.ezandro.order_service.dto.OrderRequestDTO;
import developer.ezandro.order_service.dto.OrderResponseDTO;
import developer.ezandro.order_service.dto.ProductDTO;

import java.util.List;

public interface OrderService {
    List<ProductDTO> getAvailableProducts();
    OrderResponseDTO simulateOrder(OrderRequestDTO orderRequest);
}