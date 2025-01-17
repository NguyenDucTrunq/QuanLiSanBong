	package com.manage.project.controller;
	
	import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.manage.project.model.Booking;
import com.manage.project.model.Field;
import com.manage.project.repo.BookingRepository;
import com.manage.project.repo.FieldRepository;
import com.manage.project.repo.UserRepo;
import com.manage.project.service.BookingService;
	
	@Controller
	@RequestMapping("/admin")
	public class FieldController {
	
	    @Autowired
	    private FieldRepository fieldRepository;
	    @Autowired
	    private BookingRepository bookingRepository;
	    
	    @Autowired
	    private UserRepo userRepository;
	    
	    @Autowired
	    private BookingService bookingService;
	    
	    // Hiển thị danh sách các sân
		    @GetMapping("/fields")
		    public String listFields(Model model) {
		        model.addAttribute("fields", fieldRepository.findAll());
		        return "field/list";
		    }
		
		    // Hiển thị form thêm sân mới
		    @GetMapping("/fields/create")  
		    public String addFieldForm(Model model) {
		        model.addAttribute("field", new Field());
		        return "field/create"; 
		    }
	
	    // Xử lý việc lưu thông tin sân mới
	    @PostMapping("/fields/save")  
	    public String saveField(@ModelAttribute Field field) {
	        fieldRepository.save(field);
	        return "redirect:/admin/fields";
	    }
	
	    // Hiển thị form chỉnh sửa sân
	    @GetMapping("/fields/edit/{id}")
	    public String showEditForm(@PathVariable Long id, Model model) {
	        Field field = fieldRepository.findById(id).orElseThrow();
	        model.addAttribute("field", field);
	        return "field/edit"; 
	    }
	
	    // Xử lý việc cập nhật thông tin sân
	    @PostMapping("/fields/update/{id}")
	    public String updateField(@PathVariable Long id, @ModelAttribute Field field) {
	        field.setId(id);
	        fieldRepository.save(field);
	        return "redirect:/admin/fields"; 
	    }
	 // Hiển thị trang xác nhận xóa sân
	    @GetMapping("/fields/delete/{id}")
	    public String showDeleteConfirmation(@PathVariable Long id, Model model) {
	        Field field = fieldRepository.findById(id).orElseThrow();
	        model.addAttribute("field", field);
	        return "field/delete"; 
	    }
	
	    // Xử lý việc xóa sân
	    @PostMapping("/fields/delete/{id}")
	    public String deleteField(@PathVariable Long id) {
	        fieldRepository.deleteById(id);
	        return "redirect:/admin/fields";}
	    
	    @GetMapping("/revenue-form")
	    public String showRevenueReportForm(Model model) {
	        model.addAttribute("startDate", LocalDateTime.now().minusMonths(1)); // Mặc định 1 tháng trước
	        model.addAttribute("endDate", LocalDateTime.now()); // Mặc định là thời điểm hiện tại
	        return "admin/revenue-report-form"; // Giao diện để nhập thời gian
	    }
	
	    // Xử lý báo cáo doanh thu
	    @PostMapping("/revenue")
	    public String generateRevenueReport(@RequestParam("startDate") String startDate,
	                                        @RequestParam("endDate") String endDate,
	                                        Model model) {
	        try {
	            // Sử dụng DateTimeFormatter để phân tích chuỗi có chứa dấu 'T'
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
	            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
	            LocalDateTime end = LocalDateTime.parse(endDate, formatter);

	            // Kiểm tra ngày bắt đầu không thể sau ngày kết thúc
	            if (start.isAfter(end)) {
	                model.addAttribute("error", "Ngày bắt đầu không thể sau ngày kết thúc. Vui lòng chọn lại.");
	                return "admin/revenue-report"; // Nếu có lỗi, trả về trang báo cáo
	            }

	            // Chuyển đổi LocalDateTime thành định dạng "dd/MM/yyyy HH:mm"
	            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	            // Lấy tổng doanh thu từ dịch vụ
	            Integer totalRevenue = bookingService.getTotalRevenue(start, end);

	            // Kiểm tra doanh thu có null không
	            if (totalRevenue == null || totalRevenue == 0) {	
	                model.addAttribute("error", "Không có doanh thu trong khoảng thời gian từ " + start.format(displayFormatter) + " đến " + end.format(displayFormatter));
	            } else {
	                model.addAttribute("totalRevenue", totalRevenue);
	                model.addAttribute("startDate", start.format(displayFormatter));
	                model.addAttribute("endDate", end.format(displayFormatter));
	            }
	        } catch (Exception e) {
	            model.addAttribute("error", "Có lỗi xảy ra khi tính toán doanh thu: " + e.getMessage());
	        }

	        return "admin/revenue-report"; // Giao diện hiển thị báo cáo doanh thu
	    }	
	    @GetMapping("/bookings")
	    public String showAllBookings(
	            @RequestParam(name = "startDate", required = false) String startDate,
	            @RequestParam(name = "endDate", required = false) String endDate,
	            Model model) {

	        List<Booking> bookings;

	        try {	
	            // Kiểm tra nếu cả hai ngày đều không null
	            if (startDate != null && endDate != null) {
	                LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
	                LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);

	                // Kiểm tra ràng buộc logic: ngày bắt đầu không sau ngày kết thúc
	                if (startDateTime.isAfter(endDateTime)) {
	                    model.addAttribute("error", "Ngày bắt đầu không thể sau ngày kết thúc.");
	                    return "admin/bookings-history";
	                }

	                // Lọc dữ liệu theo khoảng ngày
	                bookings = bookingService.getBookingsByDateRange(startDateTime, endDateTime);
	            } else {
	                // Nếu không có ngày được chọn, hiển thị tất cả
	                bookings = bookingService.getAllBookings();
	            }
	        } catch (DateTimeParseException e) {
	            // Xử lý lỗi định dạng ngày
	            model.addAttribute("error", "Định dạng ngày không hợp lệ. Vui lòng nhập ngày đúng định dạng (yyyy-MM-dd).");
	            return "admin/bookings-history";
	        }

	        // Truyền dữ liệu vào model
	        model.addAttribute("bookings", bookings);
	        model.addAttribute("startDate", startDate);
	        model.addAttribute("endDate", endDate);

	        return "admin/bookings-history";
	    }
	    
	}
