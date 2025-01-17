package com.manage.project.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.manage.project.model.Booking;
import com.manage.project.model.Field;
import com.manage.project.repo.BookingRepository;
import com.manage.project.repo.UserRepo;
import com.manage.project.service.BookingService;
import com.manage.project.service.UserService;

@Controller
@RequestMapping("/user")
public class UserFieldController {

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepo UserRepository;
    
    // Hiển thị danh sách sân hoạt động
    @GetMapping("home")
    public String home(Model model) {
        List<Field> activeFields = bookingService.getActiveFields();
        model.addAttribute("fields", activeFields);
        return "home"; // File: home.html
    }

    // Hiển thị form đặt sân
    @GetMapping("/book/form/{id}")
    public String showBookingForm(@PathVariable Long id, Model model) {
        Field field = bookingService.getActiveFields().stream()
                                    .filter(f -> f.getId().equals(id))
                                    .findFirst()
                                    .orElse(null);
        if (field == null) {
            model.addAttribute("error", "Sân không tồn tại.");
            return "error";
        }
        model.addAttribute("field", field);
        return "booking"; // File: booking-form.html
    }

    // Xử lý đặt sân
    @PostMapping("/book")
    public String bookField(@RequestParam("fieldId") Long fieldId,
                             @RequestParam("startTime") String startTime,
                             @RequestParam("endTime") String endTime,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
         try {
             LocalDateTime start = LocalDateTime.parse(startTime);
             LocalDateTime end = LocalDateTime.parse(endTime);
             
             Long userId = userService.findUserByUsername(principal.getName()).getUserId();
             
             Booking booking = bookingService.bookField(fieldId, start, end, userId);
             
             redirectAttributes.addFlashAttribute("message", "Đặt sân thành công.");
         } catch (Exception e) {
             redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đặt sân: " + e.getMessage());
         }
         return "redirect:/user/home";
    }


   
    @GetMapping("/history")
    public String viewBookingHistory(Model model, Principal principal) {
        // Lấy tên người dùng (username) từ Principal
        String username = principal.getName();
        
        // Tìm userId từ username
        Long userId = UserRepository.findByUsername(username).getUserId();

        // Lấy danh sách các booking của người dùng
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        // Thêm danh sách vào model để hiển thị trên giao diện
        model.addAttribute("bookings", bookings);
        return "booking/history"; // Đường dẫn đến file Thymeleaf
    }
		
}
