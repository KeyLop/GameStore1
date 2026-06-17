package com.example.gamestore1;

public class User {
    public int id;
    public String name;
    public String login;
    public String password;
    public String email;

    public User() {}

    public User(String name, String login, String password, String email) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.email = email;
    }
}