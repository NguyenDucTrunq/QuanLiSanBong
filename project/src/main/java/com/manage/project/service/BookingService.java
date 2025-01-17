package com.manage.project.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manage.project.model.Booking;
import com.manage.project.model.Field;
import com.manage.project.repo.BookingRepository;
import com.manage.project.repo.FieldRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FieldRepository fieldRepository;

    // Lấy danh sách sân hoạt động
    public List<Field> getActiveFields() {
        return fieldRepository.findByStatus(true);
    }

    // Kiểm tra xung đột giờ đặt sân
    public boolean isFieldAvailable(Long fieldId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(fieldId, startTime, endTime);
        return conflictingBookings.isEmpty();
    }
    public void validateBookingTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Thời gian đặt sân không hợp lệ.");
        }
    } 	

    // Lưu thông tin đặt sân
    public Booking bookField(Long fieldId, LocalDateTime startTime, LocalDateTime endTime, Long userId) {
        Field field = fieldRepository.findById(fieldId)
            .orElseThrow(() -> new IllegalArgumentException("Sân không tồn tại"));

        // Kiểm tra sân có khả dụng không
        if (!isFieldAvailable(fieldId, startTime, endTime)) {
            throw new IllegalStateException("Sân đã được đặt trong khoảng thời gian này.");
        }

        // Tính số giờ thuê
        long hours = java.time.Duration.between(startTime, endTime).toHours(); 

        if (hours <= 0) {
            throw new IllegalArgumentException("Thời gian đặt sân không hợp lệ.");
        }

        // Tính giá thuê theo số giờ
        Integer totalPrice = field.getPricePerHour() * (int) hours;

        // Lưu thông tin đặt sân
        Booking booking = new Booking(field, startTime, endTime, userId, totalPrice);
        return bookingRepository.save(booking);
    }
 // Phương thức tính doanh thu tổng trong khoảng thời gian
    public Integer getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.calculateTotalRevenue(startDate, endDate);
    }
    
 // Lấy toàn bộ lịch sử đặt sân
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    public List<Booking> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findByStartTimeBetween(startDate, endDate);
    }
 // Lấy thông tin đặt sân theo ID
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đặt sân với ID: " + id));
    }

    // Cập nhật thời gian đặt sân
    public Booking updateBookingTime(Long bookingId, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        // Lấy thông tin đặt sân hiện tại
        Booking booking = getBookingById(bookingId);

        // Kiểm tra tính hợp lệ của thời gian
        if (newStartTime == null || newEndTime == null || !newEndTime.isAfter(newStartTime)) {
            throw new IllegalArgumentException("Thời gian đặt sân không hợp lệ.");
        }

        // Cập nhật thông tin
        booking.setStartTime(newStartTime);
        booking.setEndTime(newEndTime);

        // Tính lại giá thuê
        long hours = java.time.Duration.between(newStartTime, newEndTime).toHours();
        if (hours <= 0) {
            throw new IllegalArgumentException("Thời gian đặt sân không hợp lệ.");
        }
        booking.setPrice(booking.getField().getPricePerHour() * (int) hours);

        return bookingRepository.save(booking);
    }
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đặt sân với ID: " + bookingId));
        
        bookingRepository.delete(booking);  // Xóa booking
    }


    

}
