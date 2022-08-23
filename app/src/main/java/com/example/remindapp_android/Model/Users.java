package com.example.remindapp_android.Model;

public class Users {
    String email;
    String name;
    String phone;

    public Users(String email, String name, String phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public Users() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
