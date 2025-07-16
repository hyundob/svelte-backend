package com.example.template_demo.entity;

import com.example.template_demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findById(String id);
}
