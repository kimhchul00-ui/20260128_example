package com.example.shop.domain.delivery.service;

import com.example.shop.domain.delivery.dto.DeliveryDto;
import com.example.shop.domain.delivery.entity.Delivery;
import com.example.shop.domain.delivery.repository.DeliveryRepository;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    public Delivery findById(Long id) {
        return deliveryRepository.findByIdWithOrder(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배송 정보입니다."));
    }

    public Page<Delivery> findAll(Pageable pageable) {
        return deliveryRepository.findAll(pageable);
    }

    public Page<Delivery> search(Delivery.DeliveryStatus status, String keyword, Pageable pageable) {
        return deliveryRepository.searchDeliveries(status, keyword, pageable);
    }

    @Transactional
    public Delivery updateStatus(Long deliveryId, DeliveryDto.UpdateRequest request) {
        Delivery delivery = findById(deliveryId);
        Delivery.DeliveryStatus newStatus = Delivery.DeliveryStatus.valueOf(request.getStatus());

        if (newStatus == Delivery.DeliveryStatus.SHIPPING) {
            delivery.ship(request.getTrackingNumber());
            // 주문 상태도 연동
            delivery.getOrder().setStatus(Order.OrderStatus.SHIPPING);
        } else if (newStatus == Delivery.DeliveryStatus.DELIVERED) {
            delivery.complete();
            delivery.getOrder().setStatus(Order.OrderStatus.DELIVERED);
        } else {
            delivery.setStatus(newStatus);
        }

        return delivery;
    }

    public long count() {
        return deliveryRepository.count();
    }

    public long countByStatus(Delivery.DeliveryStatus status) {
        return deliveryRepository.countByStatus(status);
    }
}
