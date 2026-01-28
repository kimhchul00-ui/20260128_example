package com.example.shop.domain.product.controller;

import com.example.shop.domain.product.dto.ProductDto;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                      @RequestParam(required = false) String category,
                      @RequestParam(required = false) Integer minPrice,
                      @RequestParam(required = false) Integer maxPrice,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "12") int size,
                      Model model) {

        ProductDto.SearchCondition condition = ProductDto.SearchCondition.builder()
                .keyword(keyword.isEmpty() ? null : keyword)
                .category(category)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productService.search(condition, pageable);
        List<String> categories = productService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "product/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", ProductDto.Response.from(product));
        return "product/detail";
    }
}
