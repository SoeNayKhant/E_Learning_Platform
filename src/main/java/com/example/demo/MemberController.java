package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class MemberController {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@GetMapping("/memberadd")
	public String addMember(Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id
		}
		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);
		model.addAttribute("newmember", new Member());
		return "add_member";
	}

	@PostMapping("/membersave")
	public String saveMember(@Valid Member member, BindingResult bindingResult,
			@RequestParam("memberImage") MultipartFile imgFile, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "add_member";
		}

		String imageName = imgFile.getOriginalFilename();
		// set the image name in item object
		member.setPhotoName(imageName);
		// save the item obj to the db
		Member savedMember = memberRepository.save(member);
		try {
			// prepare the directory path
			String uploadDir = "uploads/members/" + savedMember.getId();
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

		System.out.println(member.getRole() + "************************");
		if (member.getRole() == null) {
			member.setRole("ROLE_USER");
		}
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String encodePassword = bCryptPasswordEncoder.encode(member.getPassword());
		member.setPassword(encodePassword);
		memberRepository.save(member);
		redirectAttributes.addFlashAttribute("success", "Member Registered");
		return "redirect:/members";
	}

	@GetMapping("/members")
	public String viewItems(Model model) {
		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();// member id
		Member member = memberRepository.getReferenceById(loggedInMemberId);
		model.addAttribute("navmember", member);

		List<Member> memberList = memberRepository.findAll();
		model.addAttribute("memberList", memberList);

		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);

		return "view_member";
	}

	@GetMapping("/member{id}")
	public String viewSingleMember(@PathVariable("id") Integer id, Model model) {
		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();// member id
		model.addAttribute("navmember", memberRepository.getReferenceById(loggedInMemberId));

		Member member = memberRepository.getReferenceById(id);
		model.addAttribute("member", member);

		return "view_single_member";
	}

	@GetMapping("/memberedit{id}")
	public String editMember(@PathVariable("id") Integer id, Model model, Principal principal) {
		int loggedInMemberId = 0;
		if (principal != null) {
			// Get currently logged in user
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			loggedInMemberId = loggedInMember.getMember().getId();// member id

			Member member = memberRepository.getReferenceById(loggedInMemberId);
			model.addAttribute("navmember", member);
		}
		Member member = memberRepository.getReferenceById(id);
		model.addAttribute(member);
		return "edit_member";
	}

	@PostMapping("/member/edit{id}")
	public String saveMember(@PathVariable("id") Integer id, Member member,
			@RequestParam("memberImage") MultipartFile imgFile) {

		String imageName = imgFile.getOriginalFilename();

		// get existing item from the database
		Member editMember = memberRepository.getReferenceById(id);

		if (imageName.isEmpty()) {
			// No new image selected, use the existing image name
			imageName = editMember.getPhotoName();
		}

		// set the image name in item object
		member.setPhotoName(imageName);

		// save the item obj to the db
		Member savedMember = memberRepository.save(member);
		try {
			// prepare the directory path
			String uploadDir = "uploads/members/" + savedMember.getId();
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
		return "redirect:/members";
	}

	@GetMapping("/member/delete/{id}")
	public String deleteMember(@PathVariable("id") Integer id) {
		memberRepository.deleteById(id);
		return "redirect:/members";

	}
}
