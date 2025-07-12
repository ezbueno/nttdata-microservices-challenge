package developer.ezandro.order_service.controller;

import developer.ezandro.order_service.dto.OrderRequestDTO;
import developer.ezandro.order_service.dto.OrderResponseDTO;
import developer.ezandro.order_service.dto.ProductDTO;
import developer.ezandro.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping(value = "/available-products")
    public ResponseEntity<List<ProductDTO>> getAvailableProducts() {
        List<ProductDTO> products = this.orderService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }

    @PostMapping(value = "/simulate")
    public ResponseEntity<OrderResponseDTO> simulateOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        OrderResponseDTO order = this.orderService.simulateOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}