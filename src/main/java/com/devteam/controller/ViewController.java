package com.devteam.controller;

import com.devteam.entity.Book;
import com.devteam.service.BookService;
import com.devteam.service.MessageService;
import com.devteam.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.devteam.dao.BookRepository;
import com.devteam.dao.MessageRepository;
import com.devteam.dao.ReviewRepository;

@Controller
public class ViewController {

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private BookService bookService;

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }
    
    @GetMapping("/home")
    public String homePage(Model model) {
        // Get featured books for homepage
        Page<Book> books = bookRepository.findAll(PageRequest.of(0, 9));
        model.addAttribute("books", books.getContent());
        return "home";
    }
    
    @GetMapping("/search")
    public String searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            Model model) {
        
        Page<Book> bookPage;
        
        if (title != null && !title.isEmpty()) {
            bookPage = bookRepository.findByTitleContaining(title, PageRequest.of(page, size));
        } else if (category != null && !category.isEmpty()) {
            bookPage = bookRepository.findByCategory(category, PageRequest.of(page, size));
        } else {
            bookPage = bookRepository.findAll(PageRequest.of(page, size));
        }
        
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("totalItems", bookPage.getTotalElements());
        model.addAttribute("searchTitle", title);
        model.addAttribute("searchCategory", category);
        
        return "search";
    }
    
    @GetMapping("/book/{bookId}")
    public String bookDetail(@PathVariable Long bookId, Model model) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return "redirect:/search";
        }
        
        model.addAttribute("book", book);
        model.addAttribute("reviews", 
            reviewRepository.findByBookId(bookId, PageRequest.of(0, 5)).getContent());
        
        return "book-detail";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/shelf")
    public String shelf(Model model) {
        // This will need authentication
        return "shelf";
    }
    
    @GetMapping("/fees")
    public String fees(Model model) {
        // This will need authentication
        return "fees";
    }
    
    @GetMapping("/admin")
    public String admin(Model model) {
        // This will need admin authentication
        return "admin";
    }
    
    @GetMapping("/messages")
    public String messages(Model model) {
        // This will need authentication
        return "messages";
    }
} 