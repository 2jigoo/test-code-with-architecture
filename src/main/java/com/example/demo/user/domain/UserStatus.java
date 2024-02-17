package com.example.demo.user.domain;

public enum UserStatus {
    PENDING, INACTIVE, ACTIVE;

    public String getCode() {
        return name();
    }

}
