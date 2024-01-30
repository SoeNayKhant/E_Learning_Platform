package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

	public List<OrderItem> findAllByMemberId(int loggedInMemberId);

	public Optional<OrderItem> findByMemberAndCourse(Member member, Course course);
}
