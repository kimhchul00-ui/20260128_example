package com.example.shop.domain.delivery.dto;

import com.example.shop.domain.delivery.entity.Delivery;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeliveryDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @NotNull(message = "배송 상태는 필수입니다")
        private String status;

        private String trackingNumber;

        // 디폴트 값 설정
        public static UpdateRequest createDefault() {
            return UpdateRequest.builder()
                    .status("SHIPPING")
                    .trackingNumber("1234567890")
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
        private Long orderId;
        private String orderNumber;
        private String receiverName;
        private String receiverPhone;
        private String address;
        private String status;
        private String statusDescription;
        private String trackingNumber;
        private String shippedAt;
        private String deliveredAt;

        public static Response from(Delivery delivery) {
            return Response.builder()
                    .id(delivery.getId())
                    .orderId(delivery.getOrder().getId())
                    .orderNumber(delivery.getOrder().getOrderNumber())
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
}
