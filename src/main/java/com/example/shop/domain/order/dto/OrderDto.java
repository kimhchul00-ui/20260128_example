package com.example.shop.domain.order.dto;

import com.example.shop.domain.delivery.entity.Delivery;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.entity.OrderItem;
import com.example.shop.domain.order.entity.Payment;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private List<OrderItemRequest> items;

        @NotBlank(message = "수령인 이름은 필수입니다")
        private String receiverName;

        @NotBlank(message = "수령인 연락처는 필수입니다")
        private String receiverPhone;

        @NotBlank(message = "배송지 주소는 필수입니다")
        private String address;

        @NotNull(message = "결제수단은 필수입니다")
        private String paymentMethod;

        // 디폴트 값 설정
        public static CreateRequest createDefault() {
            return CreateRequest.builder()
                    .receiverName("홍길동")
                    .receiverPhone("010-1234-5678")
                    .address("서울시 강남구 테헤란로 123")
                    .paymentMethod("CREDIT_CARD")
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DirectOrderRequest {
        private Long productId;
        private Integer quantity;

        @NotBlank(message = "수령인 이름은 필수입니다")
        private String receiverName;

        @NotBlank(message = "수령인 연락처는 필수입니다")
        private String receiverPhone;

        @NotBlank(message = "배송지 주소는 필수입니다")
        private String address;

        @NotNull(message = "결제수단은 필수입니다")
        private String paymentMethod;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Integer price;
        private Integer quantity;
        private Integer totalPrice;

        public static OrderItemResponse from(OrderItem orderItem) {
            return OrderItemResponse.builder()
                    .id(orderItem.getId())
                    .productId(orderItem.getProduct().getId())
                    .productName(orderItem.getProduct().getName())
                    .price(orderItem.getPrice())
                    .quantity(orderItem.getQuantity())
                    .totalPrice(orderItem.getTotalPrice())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentResponse {
        private Long id;
        private String paymentMethod;
        private String paymentMethodDescription;
        private Integer amount;
        private String status;
        private String statusDescription;
        private String paidAt;

        public static PaymentResponse from(Payment payment) {
            if (payment == null) return null;
            return PaymentResponse.builder()
                    .id(payment.getId())
                    .paymentMethod(payment.getPaymentMethod().name())
                    .paymentMethodDescription(payment.getPaymentMethod().getDescription())
                    .amount(payment.getAmount())
                    .status(payment.getStatus().name())
                    .statusDescription(payment.getStatus().getDescription())
                    .paidAt(payment.getPaidAt() != null ? payment.getPaidAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveryResponse {
        private Long id;
        private String receiverName;
        private String receiverPhone;
        private String address;
        private String status;
        private String statusDescription;
        private String trackingNumber;
        private String shippedAt;
        private String deliveredAt;

        public static DeliveryResponse from(Delivery delivery) {
            if (delivery == null) return null;
            return DeliveryResponse.builder()
                    .id(delivery.getId())
                    .receiverName(delivery.getReceiverName())
                    .receiverPhone(delivery.getReceiverPhone())
                    .address(delivery.getAddress())
                    .status(delivery.getStatus().name())
                    .statusDescription(delivery.getStatus().getDescription())
                    .trackingNumber(delivery.getTrackingNumber())
                    .shippedAt(delivery.getShippedAt() != null ? delivery.getShippedAt().toString() : null)
                    .deliveredAt(delivery.getDeliveredAt() != null ? delivery.getDeliveredAt().toString() : null)
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
        private String orderNumber;
        private Integer totalAmount;
        private String status;
        private String statusDescription;
        private String orderedAt;
        private List<OrderItemResponse> items;
        private PaymentResponse payment;
        private DeliveryResponse delivery;

        public static Response from(Order order) {
            List<OrderItemResponse> items = order.getOrderItems().stream()
                    .map(OrderItemResponse::from)
                    .collect(Collectors.toList());

            return Response.builder()
                    .id(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus().name())
                    .statusDescription(order.getStatus().getDescription())
                    .orderedAt(order.getOrderedAt() != null ? order.getOrderedAt().toString() : null)
                    .items(items)
                    .payment(PaymentResponse.from(order.getPayment()))
                    .delivery(DeliveryResponse.from(order.getDelivery()))
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatusUpdateRequest {
        @NotNull(message = "주문 상태는 필수입니다")
        private String status;
    }
}
