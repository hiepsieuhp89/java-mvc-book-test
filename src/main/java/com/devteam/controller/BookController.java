package com.devteam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.devteam.dao.CheckoutRepository;
import com.devteam.entity.Book;
import com.devteam.entity.Checkout;
import com.devteam.entity.Review;
import com.devteam.service.BookService;
import com.devteam.service.CheckoutService;
import com.devteam.service.PaymentService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Controller
@RequestMapping("/books")
public class BookController {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private CheckoutService checkoutService;
	
	@Autowired
	private CheckoutRepository checkoutRepository;
	
	@Autowired
	private PaymentService paymentService;

	@GetMapping("/{bookId}")
	public String getBookById(@PathVariable Long bookId, Model model, 
							 @AuthenticationPrincipal UserDetails userDetails) {
		Book book = bookService.findById(bookId);
		if (book == null) {
			return "redirect:/search";
		}
		
		// Get reviews for this book
		TypedQuery<Review> query = entityManager.createQuery(
			"SELECT r FROM Review r WHERE r.bookId = :bookId", Review.class);
		query.setParameter("bookId", bookId);
		List<Review> reviews = query.getResultList();
		
		// Calculate average rating
		if (reviews != null && !reviews.isEmpty()) {
			double totalRating = 0;
			for (Review review : reviews) {
				totalRating += review.getRating();
			}
			double avgRating = totalRating / reviews.size();
			book.setAverageRating(avgRating);
			book.setReviewCount(reviews.size());
		}
		
		model.addAttribute("book", book);
		model.addAttribute("reviews", reviews);
		
		// Add checkout info if user is logged in
		if (userDetails != null) {
			String email = userDetails.getUsername();
			
			// Get current checkout for this book if exists
			Checkout currentCheckout = checkoutRepository.findByUserUsernameAndBookIdAndReturned(
				email, bookId, false);
			
			// Get checkout history for this book by this user
			List<Checkout> checkoutHistory = checkoutRepository.findByBookId(bookId);
			
			model.addAttribute("currentCheckout", currentCheckout);
			model.addAttribute("checkoutHistory", checkoutHistory);
		}
		
		return "book-detail";
	}
	
	@PostMapping("/{bookId}/checkout")
	public String checkoutBook(@PathVariable Long bookId,
							  @RequestParam("duration") int duration,
							  @RequestParam("paymentMethod") String paymentMethod,
							  @AuthenticationPrincipal UserDetails userDetails,
							  Model model) {
		
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		String email = userDetails.getUsername();
		System.out.println("Checkout request - Book ID: " + bookId + ", Duration: " + duration + ", User: " + email);
		
		try {
			// Process checkout with custom duration
			boolean success = checkoutService.checkoutBookWithDuration(email, bookId, duration);
			
			if (success) {
				// Calculate payment amount based on duration
				double amount = 0;
				switch (duration) {
					case 7: amount = 35000; break;
					case 14: amount = 60000; break;
					case 30: amount = 120000; break;
					default: amount = 35000;
				}
				
				try {
					// Create payment record
					Book book = bookService.findById(bookId);
					String description = "Thuê sách \"" + book.getTitle() + "\" - " + duration + " ngày";
					
					// Payment record
					paymentService.createPaymentRecord(email, amount, description, "RENTAL", paymentMethod, "SUCCESS");
					
					return "redirect:/payment/history?success=checkout";
				} catch (Exception e) {
					// Log the error
					System.err.println("Lỗi tạo payment: " + e.getMessage());
					e.printStackTrace();
					return "redirect:/books/" + bookId + "?error=payment";
				}
			} else {
				System.err.println("Không thể thuê sách với ID: " + bookId + " cho user: " + email);
				return "redirect:/books/" + bookId + "?error=checkout";
			}
		} catch (Exception e) {
			System.err.println("Lỗi trong quá trình thuê sách: " + e.getMessage());
			e.printStackTrace();
			return "redirect:/books/" + bookId + "?error=checkout";
		}
	}
	
	@PostMapping("/{bookId}/return")
	public String returnBook(@PathVariable Long bookId,
						   @RequestParam("condition") String condition,
						   @RequestParam(value = "damageDescription", required = false) String damageDescription,
						   @AuthenticationPrincipal UserDetails userDetails) {
		
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		String email = userDetails.getUsername();
		
		// Find the current checkout
		Checkout currentCheckout = checkoutRepository.findByUserUsernameAndBookIdAndReturned(
			email, bookId, false);
		
		if (currentCheckout != null) {
			// Process return
			boolean success = checkoutService.returnBook(currentCheckout.getId());
			
			// Handle damage fees if applicable
			if (success) {
				Book book = bookService.findById(bookId);
				
				// Add damage fee if applicable
				if ("DAMAGED".equals(condition)) {
					double damageFee = 50000; // Example fee
					String description = "Phí hư hại sách \"" + book.getTitle() + "\"";
					
					try {
						// Create payment record for damage fee
						paymentService.createPaymentRecord(email, damageFee, description, "FINE", "CASH", "SUCCESS");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				return "redirect:/payment/history?success=returned";
			}
		}
		
		return "redirect:/books/" + bookId + "?error=return";
	}
	
	@PostMapping("/{bookId}/extend")
	public String extendBook(@PathVariable Long bookId,
						   @RequestParam("duration") int duration,
						   @AuthenticationPrincipal UserDetails userDetails) {
		
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		String email = userDetails.getUsername();
		
		// Find the current checkout
		Checkout currentCheckout = checkoutRepository.findByUserUsernameAndBookIdAndReturned(
			email, bookId, false);
		
		if (currentCheckout != null) {
			// Process extension
			boolean success = checkoutService.renewBook(currentCheckout.getId());
			
			// Calculate payment amount based on duration
			if (success) {
				double amount = 0;
				switch (duration) {
					case 7: amount = 35000; break;
					case 14: amount = 60000; break;
					case 30: amount = 120000; break;
					default: amount = 35000;
				}
				
				// Create payment record for extension
				try {
					Book book = bookService.findById(bookId);
					String description = "Gia hạn sách \"" + book.getTitle() + "\" - " + duration + " ngày";
					
					// Create payment record
					paymentService.createPaymentRecord(email, amount, description, "EXTENSION", "CREDIT_CARD", "SUCCESS");
					
					return "redirect:/payment/history?success=extended";
				} catch (Exception e) {
					e.printStackTrace();
					return "redirect:/books/" + bookId + "?error=payment";
				}
			}
		}
		
		return "redirect:/books/" + bookId + "?error=extend";
	}
	
	@PostMapping("/{bookId}/review")
	public String submitReview(@PathVariable Long bookId,
							 @RequestParam("rating") int rating,
							 @RequestParam("content") String content,
							 @AuthenticationPrincipal UserDetails userDetails) {
		
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		Review review = new Review();
		review.setEmail(userDetails.getUsername());
		review.setBookId(bookId);
		review.setRating(rating);
		review.setMessage(content);
		review.setDate(new java.util.Date());
		
		entityManager.persist(review);
		
		return "redirect:/books/" + bookId + "?success=review";
	}
}
