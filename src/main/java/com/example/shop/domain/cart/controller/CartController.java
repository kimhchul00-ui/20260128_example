package com.example.shop.domain.cart.controller;

import com.example.shop.config.security.CustomUserDetails;
import com.example.shop.domain.cart.dto.CartDto;
import com.example.shop.domain.cart.entity.Cart;
import com.example.shop.domain.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String cart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Cart cart = cartService.findByMember(userDetails.getMember());
        model.addAttribute("cart", CartDto.Response.from(cart));
        return "cart/cart";
    }

    @PostMapping("/add")
    public String addItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @ModelAttribute CartDto.AddRequest request,
                         RedirectAttributes redirectAttributes) {
        try {
            cartService.addItem(userDetails.getMember(), request);
            redirectAttributes.addFlashAttribute("message", "장바구니에 상품이 추가되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/add/{productId}")
    public String addItemFromProduct(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @PathVariable Long productId,
                                    @RequestParam(defaultValue = "1") Integer quantity,
                                    RedirectAttributes redirectAttributes) {
        try {
            CartDto.AddRequest request = CartDto.AddRequest.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            cartService.addItem(userDetails.getMember(), request);
            redirectAttributes.addFlashAttribute("message", "장바구니에 상품이 추가되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/update/{cartItemId}")
    public String updateQuantity(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @PathVariable Long cartItemId,
                                @RequestParam Integer quantity,
                                RedirectAttributes redirectAttributes) {
        try {
            CartDto.UpdateRequest request = CartDto.UpdateRequest.builder()
                    .quantity(quantity)
                    .build();
            cartService.updateItemQuantity(userDetails.getMember(), cartItemId, request);
            redirectAttributes.addFlashAttribute("message", "수량이 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{cartItemId}")
    public String removeItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @PathVariable Long cartItemId,
                            RedirectAttributes redirectAttributes) {
        try {
            cartService.removeItem(userDetails.getMember(), cartItemId);
            redirectAttributes.addFlashAttribute("message", "상품이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }
}
