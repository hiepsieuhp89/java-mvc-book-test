package com.devteam.service;

import com.devteam.entity.Checkout;

public interface CheckoutService {
    boolean checkoutBook(String email, Long bookId);
    boolean checkoutBookWithDuration(String email, Long bookId, int duration);
    boolean returnBook(Long checkoutId);
    boolean renewBook(Long checkoutId);
} 