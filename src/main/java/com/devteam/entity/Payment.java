package com.devteam.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Table(name = "payment")
@Data
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_email")
	private String userEmail;

	@Column(name = "amount")
	private double amount;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "type")
	private String type; // RENTAL, EXTENSION, FINE
	
	@Column(name = "method")
	private String method; // CASH, CREDIT_CARD, BANK_TRANSFER
	
	@Column(name = "status")
	private String status; // SUCCESS, PENDING, FAILED
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	private Date date;
	
	// Constructors
	public Payment() {
	}
	
	public Payment(String userEmail, double amount, String description, String type, String method, String status) {
		this.userEmail = userEmail;
		this.amount = amount;
		this.description = description;
		this.type = type;
		this.method = method;
		this.status = status;
	}
}
