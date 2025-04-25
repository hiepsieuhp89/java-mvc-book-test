package com.devteam.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Tên đăng nhập không được để trống")
    @Column(nullable = false, unique = true)
    private String username;

    @NotEmpty(message = "Mật khẩu không được để trống")
    @Column(nullable = false)
    private String password;

    @NotEmpty(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Column(nullable = false)
    private String email;

    @NotEmpty(message = "Họ và tên không được để trống")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "active")
    private boolean active = true;
    
    @Column(name = "enabled")
    private boolean enabled = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Checkout> checkouts = new ArrayList<>();

    // Constructors
    public User() {
    }

    public User(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.active = true;
        this.enabled = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        // Đồng bộ giá trị với active
        this.active = enabled;
    }

    public List<Checkout> getCheckouts() {
        return checkouts;
    }

    public void setCheckouts(List<Checkout> checkouts) {
        this.checkouts = checkouts;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // Method to check if user has a specific role
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
    
    // Add a role to user
    public void addRole(String role) {
        roles.add(role);
    }
} 