package developer.ezandro.product_service.controller;

import developer.ezandro.product_service.dto.ProductRequestDTO;
import developer.ezandro.product_service.dto.ProductResponseDTO;
import developer.ezandro.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @RequestBody ProductRequestDTO productRequestDTO,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        ProductResponseDTO productResponseDTO = this.productService.create(productRequestDTO);
        URI uri = this.buildProductUri(uriComponentsBuilder, productResponseDTO.id());
        return ResponseEntity.created(uri).body(productResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        return ResponseEntity.ok(this.productService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(this.productService.findById(id));
    }


    private URI buildProductUri(UriComponentsBuilder uriBuilder, Long productId) {
        return uriBuilder.path("/products/{id}")
                .buildAndExpand(productId)
                .toUri();
    }
}