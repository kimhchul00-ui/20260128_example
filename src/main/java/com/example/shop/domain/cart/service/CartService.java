package com.example.shop.domain.cart.service;

import com.example.shop.domain.cart.dto.CartDto;
import com.example.shop.domain.cart.entity.Cart;
import com.example.shop.domain.cart.entity.CartItem;
import com.example.shop.domain.cart.repository.CartItemRepository;
import com.example.shop.domain.cart.repository.CartRepository;
import com.example.shop.domain.member.entity.Member;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public Cart findByMember(Member member) {
        return cartRepository.findByMemberWithItems(member)
                .orElseGet(() -> createCart(member));
    }

    public Cart findByMemberId(Long memberId) {
        return cartRepository.findByMemberIdWithItems(memberId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));
    }

    @Transactional
    public Cart createCart(Member member) {
        Cart cart = Cart.builder()
                .member(member)
                .build();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart addItem(Member member, CartDto.AddRequest request) {
        Cart cart = findByMember(member);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 이미 장바구니에 있는 상품인지 확인
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            existingItem.get().addQuantity(request.getQuantity());
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.addCartItem(cartItem);
        }

        return cart;
    }

    @Transactional
    public Cart updateItemQuantity(Member member, Long cartItemId, CartDto.UpdateRequest request) {
        Cart cart = findByMember(member);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("해당 장바구니 항목에 접근할 수 없습니다.");
        }

        if (request.getQuantity() <= 0) {
            cart.removeCartItem(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
        }

        return cart;
    }

    @Transactional
    public void removeItem(Member member, Long cartItemId) {
        Cart cart = findByMember(member);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("해당 장바구니 항목에 접근할 수 없습니다.");
        }

        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(Member member) {
        Cart cart = findByMember(member);
        cart.getCartItems().clear();
    }
}
