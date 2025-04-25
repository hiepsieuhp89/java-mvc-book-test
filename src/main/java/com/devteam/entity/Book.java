package com.devteam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "book")
@Data
public class Book {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "author")
	private String author;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "copies")
	private int copies;
	
	@Column(name = "copies_available")
	private int copiesAvailable;
	
	@Column(name = "category")
	private String category;
	
	@Column(name = "image")
	private String image;
	
	@Column(name = "isbn")
	private String isbn;
	
	@Column(name = "publication_date")
	private String publicationDate;
	
	@Column(name = "language")
	private String language;
	
	@Column(name = "page_count")
	private Integer pageCount;
	
	@Column(name = "publisher")
	private String publisher;
	
	// These fields aren't persisted but can be calculated as needed
	@javax.persistence.Transient
	private Double averageRating;
	
	@javax.persistence.Transient
	private Integer reviewCount;
}
