package com.devteam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.devteam.common.PaymentRequest;
import com.devteam.entity.Payment;
import com.devteam.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;
	
	@GetMapping
	public String paymentPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		// Add any necessary payment information to the model
		return "payment";
	}
	
	@PostMapping("/create-payment-intent")
	@ResponseBody
	public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest paymentRequest, 
											  @AuthenticationPrincipal UserDetails userDetails) {
		try {
			if (userDetails == null) {
				return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
			}
			
			PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentRequest);
			
			Map<String, String> response = new HashMap<>();
			response.put("clientSecret", paymentIntent.getClientSecret());
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (StripeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/complete")
	@ResponseBody
	public ResponseEntity<?> completePayment(@AuthenticationPrincipal UserDetails userDetails) {
		try {
			if (userDetails == null) {
				return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
			}
			
			String userEmail = userDetails.getUsername();
			return paymentService.stripePayment(userEmail);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/history")
	public String paymentHistory(
			Model model, 
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			@RequestParam(value = "paymentType", required = false) String paymentType) {
		
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		String email = userDetails.getUsername();
		
		// Create pageable with sorting by date (newest first)
		Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
		
		// Get payment history with filters
		Page<Payment> payments = paymentService.getPaymentHistory(email, startDate, endDate, paymentType, pageable);
		
		// Get payment statistics
		double totalAmount = paymentService.getTotalAmount(email);
		double totalFees = paymentService.getTotalFees(email);
		int totalBooks = paymentService.getTotalBooks(email);
		
		// Add data to model
		model.addAttribute("payments", payments.getContent());
		model.addAttribute("currentPage", payments.getNumber());
		model.addAttribute("totalPages", payments.getTotalPages());
		model.addAttribute("size", size);
		
		// Add statistics to model
		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("totalFees", totalFees);
		model.addAttribute("totalBooks", totalBooks);
		
		return "payment-history";
	}
}
