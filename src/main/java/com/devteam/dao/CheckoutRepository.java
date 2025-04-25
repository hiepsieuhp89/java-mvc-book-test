package com.devteam.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devteam.entity.Checkout;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
    
    // Find all checkouts by user email
    List<Checkout> findByUserEmail(String email);
    
    // Find current checkout for a specific book and user
    @Query("SELECT c FROM Checkout c WHERE c.user.username = :username AND c.book.id = :bookId AND c.returned = :returned")
    Checkout findByUserUsernameAndBookIdAndReturned(@Param("username") String username, @Param("bookId") Long bookId, @Param("returned") boolean returned);
    
    // Find all current checkouts for a user (not returned)
    @Query("SELECT c FROM Checkout c WHERE c.user.username = :username AND c.returned = :returned")
    List<Checkout> findByUserUsernameAndReturned(@Param("username") String username, @Param("returned") boolean returned);
    
    // Find checkout history for a specific book
    List<Checkout> findByBookId(Long bookId);
    
    // Count active checkouts for a user
    @Query("SELECT COUNT(c) FROM Checkout c WHERE c.user.username = :username AND c.returned = :returned")
    Long countByUserUsernameAndReturned(@Param("username") String username, @Param("returned") boolean returned);
} 