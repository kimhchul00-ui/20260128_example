package com.example.shop.domain.member.controller;

import com.example.shop.config.security.CustomUserDetails;
import com.example.shop.domain.member.dto.MemberDto;
import com.example.shop.domain.member.entity.Member;
import com.example.shop.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("request", MemberDto.RegisterRequest.createDefault());
        return "member/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("request") MemberDto.RegisterRequest request,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "member/register";
        }

        try {
            memberService.register(request);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/member/login";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return "member/register";
        }
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (logout != null) {
            model.addAttribute("message", "로그아웃되었습니다.");
        }
        return "member/login";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Member member = memberService.findById(userDetails.getMember().getId());
        model.addAttribute("member", MemberDto.Response.from(member));
        return "member/profile";
    }

    @GetMapping("/profile/edit")
    public String editForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Member member = memberService.findById(userDetails.getMember().getId());
        MemberDto.UpdateRequest request = MemberDto.UpdateRequest.builder()
                .name(member.getName())
                .phone(member.getPhone())
                .address(member.getAddress())
                .build();
        model.addAttribute("request", request);
        return "member/edit";
    }

    @PostMapping("/profile/edit")
    public String edit(@AuthenticationPrincipal CustomUserDetails userDetails,
                      @Valid @ModelAttribute("request") MemberDto.UpdateRequest request,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "member/edit";
        }

        try {
            memberService.update(userDetails.getMember().getId(), request);
            redirectAttributes.addFlashAttribute("message", "회원정보가 수정되었습니다.");
            return "redirect:/member/profile";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("error", e.getMessage());
            return "member/edit";
        }
    }
}
