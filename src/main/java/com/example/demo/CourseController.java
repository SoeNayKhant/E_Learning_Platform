package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Controller
public class CourseController {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private VideoRepository videoRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@GetMapping("/courses")
	public String viewCourses(Model model, Principal principal) {
		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();// member id
		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		// Get shopping cart items added by this user
		// *Hint: You will need to use the method we added in the CartItemRepository
		List<CartItem> cartItemList = cartItemRepository.findAllByMemberId(loggedInMemberId);

		int count = 0;
		for (CartItem cartItem : cartItemList) {
			count++;
		}

		// Add the shopping cart total to the model
		model.addAttribute("count", count);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		List<Course> courseList = courseRepository.findAll();// selcet * from category
		model.addAttribute("courseList", courseList);

		// Check if the user has ordered each course
		List<Boolean> orderStatusList = new ArrayList<>();
		for (Course course : courseList) {
			boolean hasOrdered = hasOrdered(loggedInMemberId, course);
			orderStatusList.add(hasOrdered);
		}

		model.addAttribute("orderStatusList", orderStatusList);

		return "view_courses";
	}

	private boolean hasOrdered(int memberId, Course course) {
		Member member = memberRepository.getReferenceById(memberId);
		Optional<OrderItem> orderItem = orderItemRepository.findByMemberAndCourse(member, course);
		return orderItem.isPresent();
	}

	@GetMapping("/searchedcourse")
	public String viewSearchedCourse(@RequestParam(value = "search", required = false) String search, Model model,
			Principal principal) {
		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();// member id
		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		// Get shopping cart items added by this user
		// *Hint: You will need to use the method we added in the CartItemRepository
		List<CartItem> cartItemList = cartItemRepository.findAllByMemberId(loggedInMemberId);

		int count = 0;
		for (CartItem cartItem : cartItemList) {
			count++;
		}
		// Add the shopping cart total to the model
		model.addAttribute("count", count);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		List<Course> courseList = courseRepository.findAll();// selcet * from category
		model.addAttribute("courseList", courseList);

		// If search parameter is present, perform search
		if (search != null && !search.isEmpty()) {
			List<Course> searchResults = courseRepository.findByNameContainingIgnoreCase(search);
			model.addAttribute("searchResults", searchResults);
			model.addAttribute("search", search);
		}
		return "searched_course";
	}

	@GetMapping("/enrolledcourses")
	public String viewEnrolledCourses(Model model, Principal principal) {
		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();// member id
		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		// Get shopping cart items added by this user
		// *Hint: You will need to use the method we added in the CartItemRepository
		List<CartItem> cartItemList = cartItemRepository.findAllByMemberId(loggedInMemberId);

		int count = 0;
		for (CartItem cartItem : cartItemList) {
			count++;
		}
		// Add the shopping cart total to the model
		model.addAttribute("count", count);

		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		List<Course> courseList = courseRepository.findAll();// selcet * from category
		model.addAttribute("courseList", courseList);

		List<Course> enrolledCourses = new ArrayList<>();

		for (Course course : courseList) {
			if (hasOrdered(loggedInMemberId, course)) {
				enrolledCourses.add(course);
			}
		}
		model.addAttribute("enrolledCourses", enrolledCourses);

		return "view_enrolled_courses";
	}

	@GetMapping("/courseadd")
	public String addCourde(Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id
		}

		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		Course course = new Course();
		model.addAttribute("course", course);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "add_course";
	}

	@PostMapping("/coursesave")
	public String saveCourse(@Valid Course course, BindingResult bindingResult,
			@RequestParam("itemImage") MultipartFile imgFile, Model model, Principal principal) {
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
			return "add_course";
		}
		String imageName = imgFile.getOriginalFilename();
		// set the image name in item object
		course.setImgName(imageName);
		// save the item obj to the db
		Course savedItem = courseRepository.save(course);
		try {
			// prepare the directory path
			String uploadDir = "uploads/courses/" + savedItem.getId();
			Path uploadPath = Paths.get(uploadDir);
			// check if the upload path exists, if not it will be created
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			// prepare path for file
			Path fileToCreatePath = uploadPath.resolve(imageName);
			System.out.println("File path: " + fileToCreatePath);
			// copy file to the upload location
			Files.copy(imgFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/courses";
	}

	@GetMapping("/courses{id}")
	public String viewSingleCourse(@PathVariable("id") Integer id, Model model) {

		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();// member id
		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);

		List<CartItem> cartItemList = cartItemRepository.findAllByMemberId(loggedInMemberId);
		model.addAttribute("cartItemList", cartItemList);
		int count = 0;
		for (CartItem cartItem : cartItemList) {
			count++;
		}
		model.addAttribute("count", count);

		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);

		Course course = courseRepository.getReferenceById(id);
		model.addAttribute("course", course);

		Category category = categoryRepository.getReferenceById(id);
		model.addAttribute("category", category);

		List<Video> videoList = videoRepository.findByCourseId(id);
		model.addAttribute("videoList", videoList);

		List<Course> courseList = courseRepository.findAll();// selcet * from category
		model.addAttribute("courseList", courseList);

		boolean orderStatus = hasOrdered(loggedInMemberId, course);

		model.addAttribute("orderStatus", orderStatus);

		return "view_single_course";
	}

	@GetMapping("/courseedit{id}")
	public String editCourse(@PathVariable("id") Integer id, Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id
		}

		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		Course course = courseRepository.getReferenceById(id);
		model.addAttribute("course", course);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute(categoryList);
		return "edit_course";

	}

	@PostMapping("/course/edit/{id}")
	public String saveUpdatedItem(@PathVariable("id") Integer id, Course course,
			@RequestParam("itemImage") MultipartFile imgFile) {
		String imageName = imgFile.getOriginalFilename();

		// get existing item from the database
		Course editCourse = courseRepository.getReferenceById(id);

		if (imageName.isEmpty()) {
			// No new image selected, use the existing image name
			imageName = editCourse.getImgName();
		}

		// set the image name in item object
		course.setImgName(imageName);

		// save the item obj to the db
		Course savedCourse = courseRepository.save(course);
		try {
			// prepare the directory path
			String uploadDir = "uploads/courses/" + savedCourse.getId();
			Path uploadPath = Paths.get(uploadDir);

			// check if the upload path exists, if not it will be created
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// prepare path for file
			Path fileToCreatePath = uploadPath.resolve(imageName);

			// copy file to the upload location if a new image is provided
			if (!imgFile.isEmpty()) {
				Files.copy(imgFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/courses";
	}

	@GetMapping("/coursevideos{id}")
	public String viewSingleCategory(@PathVariable("id") Integer id, Model model) {

		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();// member id
		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);

		// Get shopping cart items added by this user
		// *Hint: You will need to use the method we added in the CartItemRepository
		List<CartItem> cartItemList = cartItemRepository.findAllByMemberId(loggedInMemberId);

		int count = 0;
		for (CartItem cartItem : cartItemList) {
			count++;
		}

		// Add the shopping cart total to the model
		model.addAttribute("count", count);

		Course course = new Course();
		model.addAttribute("course", course);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		List<Video> videoList = videoRepository.findByCourseId(id);
		model.addAttribute("videoList", videoList);
		return "view_single_course_videos";
	}

	@GetMapping("/course/delete/{id}")
	public String deleteCourse(@PathVariable("id") Integer id) {
		courseRepository.deleteById(id);
		return "redirect:/courses";

	}
}
