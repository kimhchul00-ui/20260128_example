package com.example.shop.domain.member.dto;

import com.example.shop.domain.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class MemberDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequest {
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다")
        private String password;

        @NotBlank(message = "이름은 필수입니다")
        private String name;

        private String phone;
        private String address;

        // 디폴트 값 설정
        public static RegisterRequest createDefault() {
            return RegisterRequest.builder()
                    .email("user@example.com")
                    .password("1234")
                    .name("홍길동")
                    .phone("010-1234-5678")
                    .address("서울시 강남구 테헤란로 123")
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @NotBlank(message = "이름은 필수입니다")
        private String name;
        private String phone;
        private String address;
        private String currentPassword;
        private String newPassword;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String email;
        private String name;
        private String phone;
        private String address;
        private String role;
        private String createdAt;

        public static Response from(Member member) {
            return Response.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .phone(member.getPhone())
                    .address(member.getAddress())
                    .role(member.getRole().name())
                    .createdAt(member.getCreatedAt() != null ? member.getCreatedAt().toString() : null)
                    .build();
        }
    }
}
