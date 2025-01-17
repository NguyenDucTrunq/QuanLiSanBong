package com.manage.project.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.manage.project.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Query("SELECT b FROM Booking b WHERE b.field.id = :fieldId AND " +
	           "(b.startTime < :endTime AND b.endTime > :startTime)")
	    List<Booking> findConflictingBookings(@Param("fieldId") Long fieldId, 
	                                          @Param("startTime") LocalDateTime startTime, 
	                                          @Param("endTime") LocalDateTime endTime);

    List<Booking> findByUserId(Long userId);
    
    
    @Query("SELECT b FROM Booking b WHERE b.startTime BETWEEN :startDate AND :endDate")
    List<Booking> findByStartTimeBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
 // Truy vấn để tính tổng doanh thu trong khoảng thời gian
    @Query("SELECT SUM(b.price) FROM Booking b WHERE b.startTime BETWEEN :startDate AND :endDate")
    Integer calculateTotalRevenue(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    // kiểm tra trùng lịch khi chỉnh sửa
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
    	       "WHERE b.field.id = :fieldId AND b.id != :bookingId AND " +
    	       "((:startTime BETWEEN b.startTime AND b.endTime) OR (:endTime BETWEEN b.startTime AND b.endTime) OR " +
    	       "(b.startTime BETWEEN :startTime AND :endTime))")
    	boolean existsByFieldIdAndTimeRange(@Param("fieldId") Long fieldId, 
    	                                    @Param("startTime") LocalDateTime startTime, 
    	                                    @Param("endTime") LocalDateTime endTime, 
    	                                    @Param("bookingId") Long bookingId);


}
