package com.manage.project.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.manage.project.model.Booking;
import com.manage.project.model.Field;
import com.manage.project.repo.BookingRepository;
import com.manage.project.service.BookingService;
import com.manage.project.service.FieldService;
import com.manage.project.service.UserService;

@Controller
public class BookingController {

    @Autowired
    private FieldService fieldService;

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    
    // Hiển thị form đặt sân với thông tin sân
    @GetMapping("/booking/form")
    public String showBookingForm(@RequestParam("fieldId") Long fieldId, Model model) {
        Field field = fieldService.getFieldById(fieldId);
        model.addAttribute("field", field);
        return "booking/form"; // Chuyển đến template booking/form
    }
    @PostMapping("/booking/save")
    public String saveBooking(@RequestParam("fieldId") Long fieldId,
                              @RequestParam("startTime") String startTime,
                              @RequestParam("endTime") String endTime,
                              Principal principal, 
                              RedirectAttributes redirectAttributes) {
        try {
            // Parse thời gian
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);

            // Validate thời gian
            bookingService.validateBookingTime(start, end);
            
            // Lấy thông tin người dùng
            Long userId = userService.findUserByUsername(principal.getName()).getUserId();

            // Đặt sân
            bookingService.bookField(fieldId, start, end, userId);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Đặt sân thành công!");
            return "redirect:/user/home";
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Xử lý lỗi từ validate hoặc logic đặt sân
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/user/home?fieldId=" + fieldId;
        } catch (Exception e) {
            // Xử lý lỗi chung
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/user/home?fieldId=" + fieldId;
        }
    }			

 
    
	 // Hiển thị trang xác nhận hủy đơn đặt sân
	    @GetMapping("/booking/cancel/{id}")
	    public String showCancelConfirmation(@PathVariable Long id, Model model) {
	        Booking booking = bookingService.getBookingById(id); // Lấy thông tin đặt sân theo ID
	        model.addAttribute("booking", booking); // Thêm thông tin đặt sân vào model
	        return "/booking/huy"; // Chuyển đến trang xác nhận hủy
	    }
	
	    
	    @PostMapping("/booking/cancel/{id}")
	    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	        try {
	            bookingService.cancelBooking(id); // Logic hủy đơn
	            redirectAttributes.addFlashAttribute("success", "Hủy đơn đặt sân thành công!");
	        } catch (Exception e) {
	            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi hủy đơn!");
	        }
	        return "redirect:/user/history";
	    }
}
