package com.example.shop.domain.cart.dto;

import com.example.shop.domain.cart.entity.Cart;
import com.example.shop.domain.cart.entity.CartItem;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class CartDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddRequest {
        private Long productId;
        private Integer quantity;

        // 디폴트 값 설정
        public static AddRequest createDefault() {
            return AddRequest.builder()
                    .productId(1L)
                    .quantity(1)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private Integer quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productImageUrl;
        private Integer price;
        private Integer quantity;
        private Integer totalPrice;

        public static CartItemResponse from(CartItem cartItem) {
            return CartItemResponse.builder()
                    .id(cartItem.getId())
                    .productId(cartItem.getProduct().getId())
                    .productName(cartItem.getProduct().getName())
                    .productImageUrl(cartItem.getProduct().getImageUrl())
                    .price(cartItem.getProduct().getPrice())
                    .quantity(cartItem.getQuantity())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private List<CartItemResponse> items;
        private Integer totalPrice;
        private Integer totalQuantity;

        public static Response from(Cart cart) {
            List<CartItemResponse> items = cart.getCartItems().stream()
                    .map(CartItemResponse::from)
                    .collect(Collectors.toList());

            return Response.builder()
                    .id(cart.getId())
                    .items(items)
                    .totalPrice(cart.getTotalPrice())
                    .totalQuantity(cart.getTotalQuantity())
                    .build();
        }
    }
}
