package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Controller
public class VideoController {

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

	@GetMapping("/videos")
	public String viewVideos(Model model, Principal principal) {
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
		List<Course> courseList = courseRepository.findAll();
		model.addAttribute("courseList", courseList);
		List<Video> videoList = videoRepository.findAll();// selcet * from category
		model.addAttribute("videoList", videoList);
		return "view_videos";
	}

	@GetMapping("/videoadd")
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
		Video video = new Video();
		model.addAttribute("video", video);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		List<Course> courseList = courseRepository.findAll();
		model.addAttribute("courseList", courseList);
		return "add_video";
	}

	@PostMapping("/videosave")
	public String saveVideo(@Valid Video video, BindingResult bindingResult,
			@RequestParam("courseVideo") MultipartFile vidFile, Model model, Principal principal) {
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
			return "add_video";
		}
		String videoName = vidFile.getOriginalFilename();
		// set the image name in item object
		video.setVidName(videoName);
		// save the item obj to the db
		Video savedVideo = videoRepository.save(video);
		try {
			// prepare the directory path
			String uploadDir = "uploads/videos/" + savedVideo.getId();
			Path uploadPath = Paths.get(uploadDir);
			// check if the upload path exists, if not it will be created
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			// prepare path for file
			Path fileToCreatePath = uploadPath.resolve(videoName);
			System.out.println("File path: " + fileToCreatePath);
			// copy file to the upload location
			Files.copy(vidFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/videos";
	}

	@GetMapping("/videos{id}")
	public String viewSingleVideo(@PathVariable("id") Integer id, Model model) {

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

		Video video = videoRepository.getReferenceById(id);
		model.addAttribute("video", video);

		Course course = courseRepository.getReferenceById(id);
		model.addAttribute("course", course);

		return "view_single_video";
	}

	@GetMapping("/videoedit{id}")
	public String editVideo(@PathVariable("id") Integer id, Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id
		}

		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		Course course=new Course();
		model.addAttribute(course);
		
		Video video = videoRepository.getReferenceById(id);
		model.addAttribute("video", video);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute(categoryList);
		List<Course> courseList = courseRepository.findAll();
		model.addAttribute("courseList", courseList);
		return "edit_video";

	}

	@PostMapping("/video/edit/{id}")
	public String saveUpdatedVideo(@PathVariable("id") Integer id, Video video,
			@RequestParam("courseVideo") MultipartFile vidFile) {
		String videoName = vidFile.getOriginalFilename();

		// get existing item from the database
		Video editVideo = videoRepository.getReferenceById(id);

		if (videoName.isEmpty()) {
			// No new image selected, use the existing image name
			videoName = editVideo.getVidName();
		}

		// set the image name in item object
		video.setVidName(videoName);

		// save the item obj to the db
		Video savedVideo = videoRepository.save(video);
		try {
			// prepare the directory path
			String uploadDir = "uploads/videos/" + savedVideo.getId();
			Path uploadPath = Paths.get(uploadDir);

			// check if the upload path exists, if not it will be created
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// prepare path for file
			Path fileToCreatePath = uploadPath.resolve(videoName);

			// copy file to the upload location if a new image is provided
			if (!vidFile.isEmpty()) {
				Files.copy(vidFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/videos";
	}

	@GetMapping("/video/delete/{id}")
	public String deleteVideo(@PathVariable("id") Integer id) {
		videoRepository.deleteById(id);
		return "redirect:/videos";

	}
}
