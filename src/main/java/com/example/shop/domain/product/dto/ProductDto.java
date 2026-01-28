package com.example.shop.domain.product.dto;

import com.example.shop.domain.product.entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class ProductDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "상품명은 필수입니다")
        private String name;

        private String description;

        @NotNull(message = "가격은 필수입니다")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다")
        private Integer price;

        @NotNull(message = "재고수량은 필수입니다")
        @Min(value = 0, message = "재고수량은 0 이상이어야 합니다")
        private Integer stockQuantity;

        private String category;
        private String imageUrl;

        // 디폴트 값 설정
        public static CreateRequest createDefault() {
            return CreateRequest.builder()
                    .name("샘플 상품")
                    .description("이 상품은 샘플 상품입니다.")
                    .price(10000)
                    .stockQuantity(100)
                    .category("의류")
                    .imageUrl("https://via.placeholder.com/300x300")
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @NotBlank(message = "상품명은 필수입니다")
        private String name;

        private String description;

        @NotNull(message = "가격은 필수입니다")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다")
        private Integer price;

        @NotNull(message = "재고수량은 필수입니다")
        @Min(value = 0, message = "재고수량은 0 이상이어야 합니다")
        private Integer stockQuantity;

        private String category;
        private String imageUrl;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private Integer price;
        private Integer stockQuantity;
        private String category;
        private String imageUrl;
        private Boolean isActive;
        private String createdAt;

        public static Response from(Product product) {
            return Response.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .stockQuantity(product.getStockQuantity())
                    .category(product.getCategory())
                    .imageUrl(product.getImageUrl())
                    .isActive(product.getIsActive())
                    .createdAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchCondition {
        private String keyword;
        private String category;
        private Integer minPrice;
        private Integer maxPrice;
    }
}
