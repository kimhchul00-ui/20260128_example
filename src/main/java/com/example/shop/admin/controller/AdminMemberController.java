package com.example.shop.admin.controller;

import com.example.shop.domain.member.dto.MemberDto;
import com.example.shop.domain.member.entity.Member;
import com.example.shop.domain.member.service.MemberService;
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

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "20") int size,
                      Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Member> members = memberService.search(keyword.isEmpty() ? null : keyword, pageable);

        model.addAttribute("members", members.map(MemberDto.Response::from));
        model.addAttribute("keyword", keyword);

        return "admin/member/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Member member = memberService.findById(id);
        model.addAttribute("member", MemberDto.Response.from(member));
        return "admin/member/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Member member = memberService.findById(id);
        MemberDto.UpdateRequest request = MemberDto.UpdateRequest.builder()
                .name(member.getName())
                .phone(member.getPhone())
                .address(member.getAddress())
                .build();
        model.addAttribute("member", MemberDto.Response.from(member));
        model.addAttribute("request", request);
        return "admin/member/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                      @Valid @ModelAttribute("request") MemberDto.UpdateRequest request,
                      BindingResult bindingResult,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Member member = memberService.findById(id);
            model.addAttribute("member", MemberDto.Response.from(member));
            return "admin/member/edit";
        }

        memberService.updateByAdmin(id, request);
        redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
        return "redirect:/admin/members/" + id;
    }
}
