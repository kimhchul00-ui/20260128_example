package com.example.shop.admin.controller;

import com.example.shop.domain.delivery.entity.Delivery;
import com.example.shop.domain.delivery.service.DeliveryService;
import com.example.shop.domain.member.repository.MemberRepository;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.service.OrderService;
import com.example.shop.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final MemberRepository memberRepository;
    private final ProductService productService;
    private final OrderService orderService;
    private final DeliveryService deliveryService;

    @GetMapping
    public String dashboard(Model model) {
        // 통계 정보
        model.addAttribute("totalMembers", memberRepository.count());
        model.addAttribute("totalProducts", productService.count());
        model.addAttribute("totalOrders", orderService.count());

        // 주문 상태별 카운트
        model.addAttribute("pendingOrders", orderService.countByStatus(Order.OrderStatus.PENDING));
        model.addAttribute("paidOrders", orderService.countByStatus(Order.OrderStatus.PAID));
        model.addAttribute("shippingOrders", orderService.countByStatus(Order.OrderStatus.SHIPPING));
        model.addAttribute("deliveredOrders", orderService.countByStatus(Order.OrderStatus.DELIVERED));

        // 배송 상태별 카운트
        model.addAttribute("pendingDeliveries", deliveryService.countByStatus(Delivery.DeliveryStatus.PENDING));
        model.addAttribute("shippingDeliveries", deliveryService.countByStatus(Delivery.DeliveryStatus.SHIPPING));

        return "admin/dashboard";
    }
}
