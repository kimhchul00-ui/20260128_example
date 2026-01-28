package com.example.shop.controller;

import com.example.shop.domain.product.dto.ProductDto;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        // 최신 상품 8개 조회
        Page<Product> latestProducts = productService.findActiveProducts(PageRequest.of(0, 8));
        List<ProductDto.Response> products = latestProducts.getContent().stream()
                .map(ProductDto.Response::from)
                .collect(Collectors.toList());

        model.addAttribute("products", products);
        return "index";
    }
}
