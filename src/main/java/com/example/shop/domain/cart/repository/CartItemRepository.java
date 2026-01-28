package com.example.shop.domain.cart.repository;

import com.example.shop.domain.cart.entity.Cart;
import com.example.shop.domain.cart.entity.CartItem;
import com.example.shop.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    void deleteByCartAndProduct(Cart cart, Product product);
}
