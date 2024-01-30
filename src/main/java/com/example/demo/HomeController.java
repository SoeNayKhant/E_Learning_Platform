package com.example.demo;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private CartItemRepository cartItemRepo;

	@GetMapping("/")
	public String home(Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id

			// Get shopping cart items added by this user
			// *Hint: You will need to use the method we added in the CartItemRepository
			List<CartItem> cartItemList = cartItemRepo.findAllByMemberId(loggedInMemberId);

			int count = 0;
			for (CartItem cartItem : cartItemList) {
				count++;
			}

			// Add the shopping cart total to the model
			model.addAttribute("count", count);
		}

		Category category = new Category();
		model.addAttribute("category", category);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);

		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		return "index";
	}

	@GetMapping("/service")
	public String service(Model model) {

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

		return "services";
	}

	@GetMapping("/about")
	public String aboutUs(Model model) {

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

		return "aboutUs";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/403")
	public String error403() {
		return "403";
	}
}
