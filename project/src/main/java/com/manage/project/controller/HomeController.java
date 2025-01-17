package com.manage.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.manage.project.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class HomeController {

	@Autowired
    private UserService userService;
	
	@GetMapping("/login")
	public String showLoginForm() {
	    return "login";
	}
	
	@GetMapping("home")
	public String Home(Model model) {
		return "home"; 
	}
	
	@GetMapping("/logout")
	public String Logout(HttpServletRequest request, HttpServletResponse response) {
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
	    logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
	    return "redirect:/login";
	}
	 @GetMapping("/register")
	    public String showRegisterForm() {
	        return "register"; // Trả về trang đăng ký
	    }

	 @PostMapping("/register")
	    public String registerUser(
	            @RequestParam("username") String username,
	            @RequestParam("password") String password,
	            Model model) {

	        try {
	            userService.createUser(username, password); // Gọi UserService để lưu người dùng
	            model.addAttribute("success", "Đăng ký thành công! Hãy đăng nhập.");
	            return "redirect:/login"; // Chuyển về trang đăng nhập sau khi đăng ký thành công
	        } catch (IllegalArgumentException e) {
	            model.addAttribute("error", e.getMessage()); // Hiển thị lỗi nếu tài khoản đã tồn tại
	            return "register"; // Quay lại trang đăng ký
	        }
	    }
}
