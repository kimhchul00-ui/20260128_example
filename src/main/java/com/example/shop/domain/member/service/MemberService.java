package com.example.shop.domain.member.service;

import com.example.shop.domain.cart.entity.Cart;
import com.example.shop.domain.cart.repository.CartRepository;
import com.example.shop.domain.member.dto.MemberDto;
import com.example.shop.domain.member.entity.Member;
import com.example.shop.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member register(MemberDto.RegisterRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(Member.Role.ROLE_USER)
                .build();

        Member savedMember = memberRepository.save(member);

        // 회원가입시 장바구니 자동 생성
        Cart cart = Cart.builder()
                .member(savedMember)
                .build();
        cartRepository.save(cart);

        return savedMember;
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public Member update(Long memberId, MemberDto.UpdateRequest request) {
        Member member = findById(memberId);

        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setAddress(request.getAddress());

        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getCurrentPassword() == null ||
                !passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return member;
    }

    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Page<Member> search(String keyword, Pageable pageable) {
        return memberRepository.searchMembers(keyword, pageable);
    }

    @Transactional
    public Member updateByAdmin(Long memberId, MemberDto.UpdateRequest request) {
        Member member = findById(memberId);
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setAddress(request.getAddress());
        return member;
    }
}
