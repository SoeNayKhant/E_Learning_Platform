package com.example.demo;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookLinkController {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private CourseRepository itemRepository;

	@GetMapping("/all/{id}")
	public String viewAll(@PathVariable("id") Integer id, Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id
		}

		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		List<Course> itemList = itemRepository.findAllByCategoryId(id);
		model.addAttribute("itemList", itemList);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "view_all";// view_all.html
	}

	@GetMapping("/header")
	public String header(Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id
		}

		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "fragments/header";
	}
}
