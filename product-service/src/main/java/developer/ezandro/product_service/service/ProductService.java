package developer.ezandro.product_service.service;

import developer.ezandro.product_service.dto.ProductRequestDTO;
import developer.ezandro.product_service.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO create(ProductRequestDTO productRequestDTO);
    List<ProductResponseDTO> findAll();
    ProductResponseDTO findById(Long id);
}