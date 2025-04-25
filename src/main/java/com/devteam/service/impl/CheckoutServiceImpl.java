package com.devteam.service.impl;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devteam.dao.BookRepository;
import com.devteam.dao.CheckoutRepository;
import com.devteam.dao.UserRepository;
import com.devteam.entity.Book;
import com.devteam.entity.Checkout;
import com.devteam.entity.User;
import com.devteam.service.CheckoutService;

@Service
@Transactional
public class CheckoutServiceImpl implements CheckoutService {

    private static final Logger logger = Logger.getLogger(CheckoutServiceImpl.class.getName());

    @Autowired
    private CheckoutRepository checkoutRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public boolean checkoutBook(String email, Long bookId) {
        try {
            logger.info("Attempting to checkout book ID: " + bookId + " for user: " + email);
            
            User user = userRepository.findByUsername(email).orElse(null);
            if (user == null) {
                logger.warning("User not found with email: " + email);
                return false;
            }
            
            Optional<Book> book = bookRepository.findById(bookId);
            if (!book.isPresent()) {
                logger.warning("Book not found with ID: " + bookId);
                return false;
            }
            
            if (book.get().getCopiesAvailable() <= 0) {
                logger.warning("No copies available for book ID: " + bookId);
                return false;
            }
            
            // Check if user already has this book checked out
            Checkout existingCheckout = checkoutRepository.findByUserUsernameAndBookIdAndReturned(email, bookId, false);
            if (existingCheckout != null) {
                logger.warning("User already has this book checked out. Checkout ID: " + existingCheckout.getId());
                return false;
            }
            
            // Create new checkout
            Checkout checkout = new Checkout();
            checkout.setUser(user);
            checkout.setBook(book.get());
            checkout.setCheckoutDate(new Date());
            
            // Default return date is 7 days from now
            LocalDate returnDate = LocalDate.now().plusDays(7);
            checkout.setReturnDate(java.sql.Date.valueOf(returnDate));
            checkout.setReturned(false);
            
            checkoutRepository.save(checkout);
            logger.info("Checkout created successfully with ID: " + checkout.getId());
            
            // Update book available copies
            book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
            bookRepository.save(book.get());
            logger.info("Book copies updated. New available copies: " + book.get().getCopiesAvailable());
            
            return true;
        } catch (Exception e) {
            logger.severe("Error during book checkout: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean returnBook(Long checkoutId) {
        try {
            logger.info("Attempting to return book for checkout ID: " + checkoutId);
            
            Optional<Checkout> checkout = checkoutRepository.findById(checkoutId);
            if (!checkout.isPresent()) {
                logger.warning("Checkout not found with ID: " + checkoutId);
                return false;
            }
            
            // Mark as returned
            checkout.get().setReturned(true);
            checkout.get().setReturnedDate(new Date());
            checkoutRepository.save(checkout.get());
            logger.info("Checkout marked as returned for ID: " + checkoutId);
            
            // Update book available copies
            Book book = checkout.get().getBook();
            book.setCopiesAvailable(book.getCopiesAvailable() + 1);
            bookRepository.save(book);
            logger.info("Book copies updated. New available copies: " + book.getCopiesAvailable());
            
            return true;
        } catch (Exception e) {
            logger.severe("Error during book return: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean renewBook(Long checkoutId) {
        try {
            logger.info("Attempting to renew book for checkout ID: " + checkoutId);
            
            Optional<Checkout> checkout = checkoutRepository.findById(checkoutId);
            if (!checkout.isPresent()) {
                logger.warning("Checkout not found with ID: " + checkoutId);
                return false;
            }
            
            if (checkout.get().isReturned()) {
                logger.warning("Cannot renew already returned book. Checkout ID: " + checkoutId);
                return false;
            }
            
            // Extend return date by 7 days from current return date
            Date currentReturnDate = checkout.get().getReturnDate();
            LocalDate newReturnDate = LocalDate.parse(currentReturnDate.toString()).plusDays(7);
            checkout.get().setReturnDate(java.sql.Date.valueOf(newReturnDate));
            
            // Increment renewal count
            checkout.get().setRenewalCount(checkout.get().getRenewalCount() + 1);
            
            checkoutRepository.save(checkout.get());
            logger.info("Checkout renewed successfully. New return date: " + newReturnDate);
            
            return true;
        } catch (Exception e) {
            logger.severe("Error during book renewal: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean checkoutBookWithDuration(String email, Long bookId, int duration) {
        try {
            logger.info("Attempting to checkout book with custom duration - Book ID: " + bookId + ", User: " + email + ", Duration: " + duration);
            
            User user = userRepository.findByUsername(email).orElse(null);
            if (user == null) {
                logger.warning("User not found with email: " + email);
                return false;
            }
            
            Optional<Book> book = bookRepository.findById(bookId);
            if (!book.isPresent()) {
                logger.warning("Book not found with ID: " + bookId);
                return false;
            }
            
            // Check if user already has this book checked out
            Checkout existingCheckout = checkoutRepository.findByUserUsernameAndBookIdAndReturned(email, bookId, false);
            if (existingCheckout != null) {
                logger.warning("User already has this book checked out. Checkout ID: " + existingCheckout.getId());
                return false;
            }

            Book selectedBook = book.get();
            if (selectedBook.getCopiesAvailable() <= 0) {
                logger.warning("No copies available for book ID: " + bookId);
                return false;
            }
            
            // Create new checkout
            Checkout checkout = new Checkout();
            checkout.setUser(user);
            checkout.setBook(selectedBook);
            checkout.setCheckoutDate(new Date());
            
            // Set return date based on duration parameter
            LocalDate returnDate = LocalDate.now().plusDays(duration);
            checkout.setReturnDate(java.sql.Date.valueOf(returnDate));
            checkout.setReturned(false);
            
            checkoutRepository.save(checkout);
            logger.info("Checkout created successfully with custom duration: " + duration + " days");
            
            // Update book available copies
            selectedBook.setCopiesAvailable(selectedBook.getCopiesAvailable() - 1);
            bookRepository.save(selectedBook);
            logger.info("Book copies updated. New available copies: " + selectedBook.getCopiesAvailable());
            
            return true;
        } catch (Exception e) {
            logger.severe("Error during book checkout with custom duration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 