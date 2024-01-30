package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;


@Controller
public class CategoryController {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private CartItemRepository cartItemRepo;

	@GetMapping("/categories")
	public String viewCategories(Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id
		}

		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		List<Category> categoryList = categoryRepository.findAll();// selcet * from category
		model.addAttribute("categoryList", categoryList);
		return "view_categories";
	}

	@GetMapping("/categoryadd")
	public String addCategory(Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id
		}

		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
//		Category category=new Category();
//		model.addAllAttributes("category",category);
		model.addAttribute("category", new Category());
		return "add_category";
	}

	@PostMapping("/categorysave")
	public String saveCategory(@Valid Category c, BindingResult bindingResult, Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id
		}

		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		if (bindingResult.hasErrors()) {
			return "add_category";
		}
		categoryRepository.save(c);
		return "redirect:/categories";
	}

	@GetMapping("/categories{id}")
	public String viewSingleCategory(@PathVariable("id") Integer id, Model model) {

		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();// member id
		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);

		// Get shopping cart items added by this user
		// *Hint: You will need to use the method we added in the CartItemRepository
		List<CartItem> cartItemList = cartItemRepo.findAllByMemberId(loggedInMemberId);

		int count = 0;
		for (CartItem cartItem : cartItemList) {
			count++;
		}

		// Add the shopping cart total to the model
		model.addAttribute("count", count);

		Category category = new Category();
		model.addAttribute("category", category);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		List<Course> courseList = courseRepository.findAllByCategoryId(id);
		model.addAttribute("courseList", courseList);
		return "view_single_category";
	}

	@GetMapping("/categoryedit{id}")
	public String editCategory(@PathVariable("id") Integer id, Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id
		}

		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		Category category = categoryRepository.getReferenceById(id);
		model.addAttribute("category", category);
		return "edit_category";

	}

	@PostMapping("/category/edit/{id}")
	public String saveUpdatedCategoy(Category category) {
		categoryRepository.save(category);
		return "redirect:/categories";
	}

	@GetMapping("/category/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id) {
		categoryRepository.deleteById(id);
		return "redirect:/categories";

	}

}
