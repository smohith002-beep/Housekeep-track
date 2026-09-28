package com.example.housekeeptrack.dto;

import com.example.housekeeptrack.entity.Guest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class GuestDTO {
    private Long id;

    @NotBlank(message = "Guest name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @Email(message = "Valid email is required")
    private String email;

    private String address;

    public GuestDTO() {
    }

    public GuestDTO(Guest guest) {
        this.id = guest.getId();
        this.name = guest.getName();
        this.phone = guest.getPhone();
        this.email = guest.getEmail();
        this.address = guest.getAddress();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
