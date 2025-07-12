package developer.ezandro.product_service.config;

import developer.ezandro.product_service.model.Product;
import developer.ezandro.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (this.productRepository.count() == 0) {
            this.productRepository.save(new Product(null, "Product A", "Description A", 10.50));
            this.productRepository.save(new Product(null, "Product B", "Description B", 20.00));
            this.productRepository.save(new Product(null, "Product C", "Description C", 30.75));
        }
    }
}