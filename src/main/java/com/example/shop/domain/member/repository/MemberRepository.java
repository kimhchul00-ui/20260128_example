package com.example.shop.domain.member.repository;

import com.example.shop.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m WHERE " +
           "(:keyword IS NULL OR m.email LIKE %:keyword% OR m.name LIKE %:keyword%)")
    Page<Member> searchMembers(@Param("keyword") String keyword, Pageable pageable);
}
