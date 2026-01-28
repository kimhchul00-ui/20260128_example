package com.example.shop.domain.order.repository;

import com.example.shop.domain.member.entity.Member;
import com.example.shop.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMemberOrderByOrderedAtDesc(Member member);

    Page<Order> findByMemberOrderByOrderedAtDesc(Member member, Pageable pageable);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.product " +
           "LEFT JOIN FETCH o.payment " +
           "LEFT JOIN FETCH o.delivery " +
           "WHERE o.id = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:keyword IS NULL OR o.orderNumber LIKE %:keyword%)")
    Page<Order> searchOrders(
            @Param("status") Order.OrderStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    long countByStatus(Order.OrderStatus status);
}
