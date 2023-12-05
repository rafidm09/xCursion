package com.example.backend.domain;

public class Ticket {
    private int ticketID;
    private Flight flight;
    private Seat seat;
    private double price;

    public Ticket(int ticketID, Flight flight, Seat seat) {
        this.ticketID = ticketID;
        this.flight = flight;
        this.seat = seat;
    }

    public Ticket() {}

    /*
    * Getters and Setters
    */

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int id) {
        ticketID = id;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
