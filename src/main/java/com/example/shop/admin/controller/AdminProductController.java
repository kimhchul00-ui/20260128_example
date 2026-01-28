package com.example.shop.admin.controller;

import com.example.shop.domain.product.dto.ProductDto;
import com.example.shop.domain.product.entity.Product;
import com.example.shop.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                      @RequestParam(required = false) String category,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "20") int size,
                      Model model) {
        ProductDto.SearchCondition condition = ProductDto.SearchCondition.builder()
                .keyword(keyword.isEmpty() ? null : keyword)
                .category(category)
                .build();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productService.search(condition, pageable);
        List<String> categories = productService.getAllCategories();

        model.addAttribute("products", products.map(ProductDto.Response::from));
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);

        return "admin/product/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("request", ProductDto.CreateRequest.createDefault());
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/product/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("request") ProductDto.CreateRequest request,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/product/form";
        }

        Product product = productService.create(request);
        redirectAttributes.addFlashAttribute("message", "상품이 등록되었습니다.");
        return "redirect:/admin/products/" + product.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", ProductDto.Response.from(product));
        return "admin/product/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        ProductDto.UpdateRequest request = ProductDto.UpdateRequest.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .build();
        model.addAttribute("product", ProductDto.Response.from(product));
        model.addAttribute("request", request);
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/product/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                      @Valid @ModelAttribute("request") ProductDto.UpdateRequest request,
                      BindingResult bindingResult,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Product product = productService.findById(id);
            model.addAttribute("product", ProductDto.Response.from(product));
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/product/edit";
        }

        productService.update(id, request);
        redirectAttributes.addFlashAttribute("message", "상품 정보가 수정되었습니다.");
        return "redirect:/admin/products/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.delete(id);
        redirectAttributes.addFlashAttribute("message", "상품이 삭제(비활성화)되었습니다.");
        return "redirect:/admin/products";
    }
}
