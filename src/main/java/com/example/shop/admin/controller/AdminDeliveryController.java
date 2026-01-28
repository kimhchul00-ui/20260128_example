package com.example.shop.admin.controller;

import com.example.shop.domain.delivery.dto.DeliveryDto;
import com.example.shop.domain.delivery.entity.Delivery;
import com.example.shop.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/deliveries")
@RequiredArgsConstructor
public class AdminDeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                      @RequestParam(required = false) String status,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "20") int size,
                      Model model) {
        Delivery.DeliveryStatus deliveryStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                deliveryStatus = Delivery.DeliveryStatus.valueOf(status);
            } catch (IllegalArgumentException ignored) {}
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Delivery> deliveries = deliveryService.search(deliveryStatus, keyword.isEmpty() ? null : keyword, pageable);

        model.addAttribute("deliveries", deliveries.map(DeliveryDto.Response::from));
        model.addAttribute("statuses", Delivery.DeliveryStatus.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "admin/delivery/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Delivery delivery = deliveryService.findById(id);
        model.addAttribute("delivery", DeliveryDto.Response.from(delivery));
        model.addAttribute("statuses", Delivery.DeliveryStatus.values());
        model.addAttribute("updateRequest", DeliveryDto.UpdateRequest.createDefault());
        return "admin/delivery/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                              @ModelAttribute DeliveryDto.UpdateRequest request,
                              RedirectAttributes redirectAttributes) {
        try {
            deliveryService.updateStatus(id, request);
            redirectAttributes.addFlashAttribute("message", "배송 상태가 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/deliveries/" + id;
    }
}
