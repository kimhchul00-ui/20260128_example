package com.example.shop.domain.delivery.repository;

import com.example.shop.domain.delivery.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("SELECT d FROM Delivery d JOIN FETCH d.order WHERE d.id = :id")
    Optional<Delivery> findByIdWithOrder(@Param("id") Long id);

    Page<Delivery> findByStatus(Delivery.DeliveryStatus status, Pageable pageable);

    @Query("SELECT d FROM Delivery d JOIN FETCH d.order o WHERE " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:keyword IS NULL OR o.orderNumber LIKE CONCAT('%', :keyword, '%') OR d.receiverName LIKE CONCAT('%', :keyword, '%'))")
    Page<Delivery> searchDeliveries(
            @Param("status") Delivery.DeliveryStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);

    long countByStatus(Delivery.DeliveryStatus status);
}
