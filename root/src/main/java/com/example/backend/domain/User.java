package com.example.backend.domain;

import java.util.ArrayList;

public class User {
    private int userID;
    private String name;
    private String email;
    private String type;
    private ArrayList<Ticket> tickets;

    public User(int userID, String name, String email, String type, ArrayList<Ticket> tickets) {
        this.userID = userID;
        this.email = email;
        this.name = name;
        this.type = type;
        this.tickets = tickets;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int id) {
        userID = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }
}