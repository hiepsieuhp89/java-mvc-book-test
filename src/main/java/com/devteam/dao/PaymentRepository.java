package com.devteam.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devteam.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
	
	// Find payment by user email
	Payment findByUserEmail(String userEmail);
	
	// Find all payments by user email
	List<Payment> findAllByUserEmail(String userEmail);
	
	// Find payments by user email with pagination
	Page<Payment> findByUserEmail(String userEmail, Pageable pageable);
	
	// Find payments by user email and date range
	@Query("SELECT p FROM Payment p WHERE p.userEmail = :email AND p.date BETWEEN :startDate AND :endDate")
	Page<Payment> findByUserEmailAndDateBetween(
			@Param("email") String email, 
			@Param("startDate") Date startDate, 
			@Param("endDate") Date endDate, 
			Pageable pageable);
	
	// Find payments by user email and payment type
	@Query("SELECT p FROM Payment p WHERE p.userEmail = :email AND p.type = :type")
	Page<Payment> findByUserEmailAndType(
			@Param("email") String email, 
			@Param("type") String type, 
			Pageable pageable);
	
	// Find payments by user email, date range, and payment type
	@Query("SELECT p FROM Payment p WHERE p.userEmail = :email AND p.date BETWEEN :startDate AND :endDate AND p.type = :type")
	Page<Payment> findByUserEmailAndDateBetweenAndType(
			@Param("email") String email, 
			@Param("startDate") Date startDate, 
			@Param("endDate") Date endDate, 
			@Param("type") String type, 
			Pageable pageable);
	
	// Calculate total amount paid by user
	@Query("SELECT SUM(p.amount) FROM Payment p WHERE p.userEmail = :email")
	Double calculateTotalAmountByUserEmail(@Param("email") String email);
	
	// Calculate total amount paid by user for a specific type
	@Query("SELECT SUM(p.amount) FROM Payment p WHERE p.userEmail = :email AND p.type = :type")
	Double calculateTotalAmountByUserEmailAndType(@Param("email") String email, @Param("type") String type);
	
	// Count total books rented by user
	@Query("SELECT COUNT(p) FROM Payment p WHERE p.userEmail = :email AND p.type = 'RENTAL'")
	Integer countTotalBooksByUserEmail(@Param("email") String email);
}
