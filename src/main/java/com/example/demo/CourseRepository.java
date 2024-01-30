package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository  extends JpaRepository<Course,Integer>{

	public List<Course> findAllByCategoryId(Integer id);

	public List<Course> findByNameContainingIgnoreCase(String search);
}
