package com.example.demo.model;

public enum UserStatus {
    PENDING, INACTIVE, ACTIVE;

    public String getCode() {
        return name();
    }

}
