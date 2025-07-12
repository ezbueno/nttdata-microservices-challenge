package developer.ezandro.product_service.service;

import developer.ezandro.product_service.dto.ProductRequestDTO;
import developer.ezandro.product_service.dto.ProductResponseDTO;
import developer.ezandro.product_service.exception.ProductNotFoundException;
import developer.ezandro.product_service.model.Product;
import developer.ezandro.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public ProductResponseDTO create(ProductRequestDTO productRequestDTO) {
        Product product = new Product(
                null,
                productRequestDTO.name(),
                productRequestDTO.description(),
                productRequestDTO.price()
        );
        return new ProductResponseDTO(this.productRepository.save(product));
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        return this.productRepository.findAll()
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        Product product = this.productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException(String.format("Product not found with ID: %s", id))
        );
        return new ProductResponseDTO(product);
    }
}