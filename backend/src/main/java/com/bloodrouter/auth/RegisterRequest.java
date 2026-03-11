package com.bloodrouter.auth;

import com.bloodrouter.common.enums.Role;

public class RegisterRequest {

    private String email;
    private String phone;
    private String password;
    private Role role;

    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
}