package developer.ezandro.order_service.client;

import developer.ezandro.order_service.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "product-service")
public interface ProductClient {
    @GetMapping(value = "/products")
    List<ProductDTO> findAll();

    @GetMapping(value = "/products/{id}")
    ProductDTO findById(@PathVariable(value = "id") Long id);
}