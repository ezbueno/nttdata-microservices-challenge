package developer.ezandro.order_service.service;

import developer.ezandro.order_service.client.ProductClient;
import developer.ezandro.order_service.dto.*;
import developer.ezandro.order_service.exception.OrderSimulationException;
import developer.ezandro.order_service.exception.ProductNotFoundException;
import developer.ezandro.order_service.model.Order;
import developer.ezandro.order_service.model.OrderItem;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final ProductClient productClient;

    private final AtomicLong orderIdGenerator = new AtomicLong(1);
    private final AtomicLong itemIdGenerator = new AtomicLong(1);

    @Override
    public List<ProductDTO> getAvailableProducts() {
        try {
            return this.productClient.findAll();
        } catch (FeignException _) {
            throw new OrderSimulationException("Unable to fetch available products from product service");
        }
    }

    @Override
    public OrderResponseDTO simulateOrder(OrderRequestDTO orderRequest) {
        if (orderRequest.items() == null || orderRequest.items().isEmpty()) {
            throw new OrderSimulationException("Order must have at least one item");
        }

        Long orderId = this.orderIdGenerator.getAndIncrement();

        List<OrderItem> orderItems = orderRequest.items().stream()
                .map(this::createOrderItem)
                .toList();

        Order order = new Order(orderId, orderItems);

        return mapToOrderResponseDTO(order);
    }

    private OrderItem createOrderItem(OrderItemRequestDTO itemRequest) {
        try {
            ProductDTO product = this.productClient.findById(itemRequest.productId());

            return new OrderItem(
                    this.itemIdGenerator.getAndIncrement(),
                    itemRequest.productId(),
                    product.name(),
                    itemRequest.quantity(),
                    BigDecimal.valueOf(product.price())
            );
        } catch (FeignException.NotFound _) {
            throw new ProductNotFoundException("Product not found with id: " + itemRequest.productId());
        } catch (FeignException _) {
            throw new OrderSimulationException("Unable to validate product with id: " + itemRequest.productId());
        }
    }

    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                itemDTOs
        );
    }
}