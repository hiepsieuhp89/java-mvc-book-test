package com.devteam.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.devteam.entity.Book;
import com.devteam.entity.Review;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Controller
@RequestMapping("/books")
public class BookController {

	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping("/{bookId}")
	public String getBookById(@PathVariable Long bookId, Model model) {
		Book book = entityManager.find(Book.class, bookId);
		if (book == null) {
			return "redirect:/search";
		}
		
		// Get reviews for this book
		TypedQuery<Review> query = entityManager.createQuery(
			"SELECT r FROM Review r WHERE r.bookId = :bookId", Review.class);
		query.setParameter("bookId", bookId);
		List<Review> reviews = query.getResultList();
		
		model.addAttribute("book", book);
		model.addAttribute("reviews", reviews);
		
		return "book-detail";
	}
	
	@GetMapping("/checkout/{bookId}")
	public String checkoutBook(@PathVariable Long bookId, 
							  @AuthenticationPrincipal UserDetails userDetails,
							  Model model) {
		
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		// Logic for checkout would go here
		// For now, just redirect back to the book page
		return "redirect:/books/" + bookId + "?success=checkout";
	}
	
	@GetMapping("/return/{bookId}")
	public String returnBook(@PathVariable Long bookId,
						   @AuthenticationPrincipal UserDetails userDetails,
						   Model model) {
		
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		// Logic for return would go here
		// For now, just redirect back to the shelf page
		return "redirect:/shelf?success=returned";
	}
	
	@GetMapping("/renew/{bookId}")
	public String renewBook(@PathVariable Long bookId,
						  @AuthenticationPrincipal UserDetails userDetails,
						  Model model) {
		
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		// Logic for renewal would go here
		// For now, just redirect back to the shelf page
		return "redirect:/shelf?success=renewed";
	}
	
	@GetMapping("/review/{bookId}")
	public String showReviewForm(@PathVariable Long bookId, Model model) {
		Book book = entityManager.find(Book.class, bookId);
		if (book == null) {
			return "redirect:/search";
		}
		
		model.addAttribute("book", book);
		model.addAttribute("review", new Review());
		
		return "review-form";
	}
	
	@PostMapping("/review/{bookId}")
	public String submitReview(@PathVariable Long bookId,
							 @ModelAttribute Review review,
							 @AuthenticationPrincipal UserDetails userDetails) {
		
		if (userDetails == null) {
			return "redirect:/login";
		}
		
		review.setEmail(userDetails.getUsername());
		review.setBookId(bookId);
		
		entityManager.persist(review);
		
		return "redirect:/books/" + bookId + "?success=review";
	}
}
