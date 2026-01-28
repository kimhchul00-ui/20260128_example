package com.example.shop.domain.order.controller;

import com.example.shop.config.security.CustomUserDetails;
import com.example.shop.domain.cart.dto.CartDto;
import com.example.shop.domain.cart.entity.Cart;
import com.example.shop.domain.cart.service.CartService;
import com.example.shop.domain.member.entity.Member;
import com.example.shop.domain.order.dto.OrderDto;
import com.example.shop.domain.order.entity.Order;
import com.example.shop.domain.order.entity.Payment;
import com.example.shop.domain.order.service.OrderService;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Random;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final ProductService productService;

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Member member = userDetails.getMember();
        Cart cart = cartService.findByMember(member);

        if (cart.getCartItems().isEmpty()) {
            return "redirect:/cart";
        }

        OrderDto.CreateRequest request = OrderDto.CreateRequest.builder()
                .receiverName(member.getName())
                .receiverPhone(member.getPhone())
                .address(member.getAddress())
                .paymentMethod(getRandomPaymentMethod())
                .build();

        model.addAttribute("cart", CartDto.Response.from(cart));
        model.addAttribute("request", request);
        model.addAttribute("paymentMethods", Payment.PaymentMethod.values());

        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @Valid @ModelAttribute("request") OrderDto.CreateRequest request,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Cart cart = cartService.findByMember(userDetails.getMember());
            model.addAttribute("cart", CartDto.Response.from(cart));
            model.addAttribute("paymentMethods", Payment.PaymentMethod.values());
            return "order/checkout";
        }

        try {
            Order order = orderService.createFromCart(userDetails.getMember(), request);
            return "redirect:/order/complete/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/order/checkout";
        }
    }

    @GetMapping("/direct/{productId}")
    public String directOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long productId,
                             @RequestParam(defaultValue = "1") Integer quantity,
                             Model model) {
        Member member = userDetails.getMember();
        Product product = productService.findById(productId);

        OrderDto.DirectOrderRequest request = OrderDto.DirectOrderRequest.builder()
                .productId(productId)
                .quantity(quantity)
                .receiverName(member.getName())
                .receiverPhone(member.getPhone())
                .address(member.getAddress())
                .paymentMethod(getRandomPaymentMethod())
                .build();

        model.addAttribute("product", product);
        model.addAttribute("quantity", quantity);
        model.addAttribute("request", request);
        model.addAttribute("paymentMethods", Payment.PaymentMethod.values());

        return "order/direct";
    }

    @PostMapping("/direct")
    public String processDirectOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @Valid @ModelAttribute("request") OrderDto.DirectOrderRequest request,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Product product = productService.findById(request.getProductId());
            model.addAttribute("product", product);
            model.addAttribute("quantity", request.getQuantity());
            model.addAttribute("paymentMethods", Payment.PaymentMethod.values());
            return "order/direct";
        }

        try {
            Order order = orderService.createDirectOrder(userDetails.getMember(), request);
            return "redirect:/order/complete/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/" + request.getProductId();
        }
    }

    @GetMapping("/complete/{orderId}")
    public String complete(@PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", OrderDto.Response.from(order));
        return "order/complete";
    }

    @GetMapping("/history")
    public String history(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderedAt"));
        Page<Order> orders = orderService.findByMember(userDetails.getMember(), pageable);
        model.addAttribute("orders", orders.map(OrderDto.Response::from));
        return "order/history";
    }

    @GetMapping("/{orderId}")
    public String detail(@AuthenticationPrincipal CustomUserDetails userDetails,
                        @PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId);

        // 본인의 주문만 볼 수 있도록
        if (!order.getMember().getId().equals(userDetails.getMember().getId())) {
            return "redirect:/order/history";
        }

        model.addAttribute("order", OrderDto.Response.from(order));
        return "order/detail";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancel(@AuthenticationPrincipal CustomUserDetails userDetails,
                        @PathVariable Long orderId,
                        RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.findById(orderId);
            if (!order.getMember().getId().equals(userDetails.getMember().getId())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/order/history";
            }
            orderService.cancel(orderId);
            redirectAttributes.addFlashAttribute("message", "주문이 취소되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/order/" + orderId;
    }

    private String getRandomPaymentMethod() {
        Payment.PaymentMethod[] methods = Payment.PaymentMethod.values();
        return methods[new Random().nextInt(methods.length)].name();
    }
}
