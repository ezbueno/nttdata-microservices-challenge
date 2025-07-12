package developer.ezandro.product_service.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name must be between {min} and {max} characters")
        String name,

        @Size(max = 500, message = "Description must not exceed {max} characters")
        String description,

        @Positive(message = "Price must be greater than zero")
        @Digits(integer = 6, fraction = 2, message = "Price must have up to {integer} integer and {fraction} decimal digits")
        double price
) {}