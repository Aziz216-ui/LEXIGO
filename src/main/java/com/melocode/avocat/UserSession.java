package com.melocode.avocat;

public class UserSession {
    private static UserSession instance;
    private String userEmail;
    private String userName;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getUserName() {
        return userName;
    }

    public void clearSession() {
        this.userEmail = null;
        this.userName = null;
    }
} 