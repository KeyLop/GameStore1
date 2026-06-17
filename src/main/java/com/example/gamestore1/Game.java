package com.example.gamestore1;

public class Game {
    public int id;
    public String title;
    public String description;
    public String developer;
    public double price;
    public double rating;

    public Game() {}

    public Game(String title, String description, double price) {
        this.title = title;
        this.description = description;
        this.price = price;
    }
}