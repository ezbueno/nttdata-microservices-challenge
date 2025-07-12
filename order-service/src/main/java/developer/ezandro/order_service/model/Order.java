package developer.ezandro.order_service.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(of = "id")
@Getter
public class Order {
    private Long id;
    private final LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private final OrderStatus status;
    private List<OrderItem> items;

    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public Order(Long id, List<OrderItem> items) {
        this();
        this.id = id;
        this.items = items;
        this.totalAmount = calculateTotalAmount();
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        this.totalAmount = calculateTotalAmount();
    }

    private BigDecimal calculateTotalAmount() {
        return this.items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}