package com.devteam.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devteam.common.PaymentRequest;
import com.devteam.dao.PaymentRepository;
import com.devteam.entity.Payment;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

@Service
@Transactional
public class PaymentService {

	private PaymentRepository paymentRepository;

	@Autowired
	public PaymentService(PaymentRepository paymentRepository, @Value("${stripe.key.secret}") String secretKey) {
		super();
		this.paymentRepository = paymentRepository;
		Stripe.apiKey = secretKey;
	}

	public PaymentIntent createPaymentIntent(PaymentRequest paymentRequest) throws StripeException {
		List<String> paymentMethod = new ArrayList<>();
		paymentMethod.add("card");

		Map<String, Object> params = new HashMap<>();
		params.put("amount", paymentRequest.getAmount());
		params.put("currency", paymentRequest.getCurrency());
		params.put("payment_method_types", paymentMethod);

		return PaymentIntent.create(params);
	}

	public ResponseEntity<String> stripePayment(String userEmail) throws Exception {
		Payment payment = paymentRepository.findByUserEmail(userEmail);

		if (payment == null) {
			throw new Exception("Payment information is missing");
		}

		payment.setAmount(00.00);
		paymentRepository.save(payment);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// Create a new payment record
	public Payment createPaymentRecord(String userEmail, double amount, String description, 
										String type, String method, String status) {
		Payment payment = new Payment(userEmail, amount, description, type, method, status);
		return paymentRepository.save(payment);
	}
	
	// Get payment history for a user
	public Page<Payment> getPaymentHistory(String userEmail, Pageable pageable) {
		return paymentRepository.findByUserEmail(userEmail, pageable);
	}
	
	// Get payment history with filters
	public Page<Payment> getPaymentHistory(String userEmail, Date startDate, Date endDate, 
											String type, Pageable pageable) {
		if (startDate != null && endDate != null && type != null && !type.isEmpty()) {
			return paymentRepository.findByUserEmailAndDateBetweenAndType(userEmail, startDate, endDate, type, pageable);
		} else if (startDate != null && endDate != null) {
			return paymentRepository.findByUserEmailAndDateBetween(userEmail, startDate, endDate, pageable);
		} else if (type != null && !type.isEmpty()) {
			return paymentRepository.findByUserEmailAndType(userEmail, type, pageable);
		} else {
			return paymentRepository.findByUserEmail(userEmail, pageable);
		}
	}
	
	// Get total amount paid by user
	public double getTotalAmount(String userEmail) {
		Double total = paymentRepository.calculateTotalAmountByUserEmail(userEmail);
		return total != null ? total : 0.0;
	}
	
	// Get total fees paid by user
	public double getTotalFees(String userEmail) {
		Double total = paymentRepository.calculateTotalAmountByUserEmailAndType(userEmail, "FINE");
		return total != null ? total : 0.0;
	}
	
	// Get total books rented by user
	public int getTotalBooks(String userEmail) {
		Integer total = paymentRepository.countTotalBooksByUserEmail(userEmail);
		return total != null ? total : 0;
	}

}
