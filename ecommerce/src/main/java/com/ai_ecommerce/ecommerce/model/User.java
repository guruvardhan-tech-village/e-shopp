package com.ai_ecommerce.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private String role;

    public User() {}

    public User(String name, String phoneNumber,String email, String password, String role){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.role = role;

    }

    //getters
     public String getName() {
        return name;
     }

     public String getPhoneNumber() {
        return phoneNumber;
     }
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    public String getRole() {
        return role;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
