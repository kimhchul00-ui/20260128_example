package com.example.shop.domain.order.service;

import com.example.shop.domain.cart.entity.Cart;
import com.example.shop.domain.cart.service.CartService;
import com.example.shop.domain.delivery.entity.Delivery;
import com.example.shop.domain.member.entity.Member;
import com.example.shop.domain.order.dto.OrderDto;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.entity.OrderItem;
import com.example.shop.domain.order.entity.Payment;
import com.example.shop.domain.order.repository.OrderRepository;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Transactional
    public Order createFromCart(Member member, OrderDto.CreateRequest request) {
        Cart cart = cartService.findByMember(member);

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("장바구니가 비어있습니다.");
        }

        Order order = Order.builder()
                .member(member)
                .status(Order.OrderStatus.PENDING)
                .totalAmount(0)
                .build();

        int totalAmount = 0;
        for (var cartItem : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.createOrderItem(
                    cartItem.getProduct(),
                    cartItem.getQuantity()
            );
            order.addOrderItem(orderItem);
            totalAmount += orderItem.getTotalPrice();
        }
        order.setTotalAmount(totalAmount);

        // Payment 생성
        Payment payment = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .amount(totalAmount)
                .status(Payment.PaymentStatus.COMPLETED)
                .build();
        payment.complete();
        order.setPayment(payment);

        // Delivery 생성
        Delivery delivery = Delivery.builder()
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .address(request.getAddress())
                .status(Delivery.DeliveryStatus.PENDING)
                .build();
        order.setDelivery(delivery);

        order.setStatus(Order.OrderStatus.PAID);

        Order savedOrder = orderRepository.save(order);

        // 장바구니 비우기
        cartService.clearCart(member);

        return savedOrder;
    }

    @Transactional
    public Order createDirectOrder(Member member, OrderDto.DirectOrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        Order order = Order.builder()
                .member(member)
                .status(Order.OrderStatus.PENDING)
                .totalAmount(0)
                .build();

        OrderItem orderItem = OrderItem.createOrderItem(product, request.getQuantity());
        order.addOrderItem(orderItem);
        order.setTotalAmount(orderItem.getTotalPrice());

        // Payment 생성
        Payment payment = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .amount(order.getTotalAmount())
                .status(Payment.PaymentStatus.COMPLETED)
                .build();
        payment.complete();
        order.setPayment(payment);

        // Delivery 생성
        Delivery delivery = Delivery.builder()
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .address(request.getAddress())
                .status(Delivery.DeliveryStatus.PENDING)
                .build();
        order.setDelivery(delivery);

        order.setStatus(Order.OrderStatus.PAID);

        return orderRepository.save(order);
    }

    public Order findById(Long id) {
        return orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
    }

    public Order findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
    }

    public List<Order> findByMember(Member member) {
        return orderRepository.findByMemberOrderByOrderedAtDesc(member);
    }

    public Page<Order> findByMember(Member member, Pageable pageable) {
        return orderRepository.findByMemberOrderByOrderedAtDesc(member, pageable);
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> search(Order.OrderStatus status, String keyword, Pageable pageable) {
        return orderRepository.searchOrders(status, keyword, pageable);
    }

    @Transactional
    public Order updateStatus(Long orderId, Order.OrderStatus status) {
        Order order = findById(orderId);
        order.setStatus(status);

        // 주문 상태에 따른 배송 상태 연동
        if (status == Order.OrderStatus.PREPARING) {
            order.getDelivery().setStatus(Delivery.DeliveryStatus.PREPARING);
        } else if (status == Order.OrderStatus.SHIPPING) {
            order.getDelivery().setStatus(Delivery.DeliveryStatus.SHIPPING);
        } else if (status == Order.OrderStatus.DELIVERED) {
            order.getDelivery().complete();
        }

        return order;
    }

    @Transactional
    public void cancel(Long orderId) {
        Order order = findById(orderId);
        order.cancel();
    }

    public long count() {
        return orderRepository.count();
    }

    public long countByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
}
