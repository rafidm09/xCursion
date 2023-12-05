package com.example.backend.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Flight {
    private String flightID;
    private String origin;
    private String destination;
    private Plane plane;
    private LocalDateTime departTime;
    private LocalDateTime arrivalTime;
    private int costReg, costFirstClass;
    private ArrayList<FlightAttendant> crewList;

    public Flight(String flightID, String origin, String destination, Plane plane, LocalDateTime departure, LocalDateTime arrival, int costReg, int costFirstClass, ArrayList<FlightAttendant> crewList) {
        this.flightID = flightID;
        this.origin = origin;
        this.destination = destination;
        this.plane = plane;
        this.departTime = departure;
        this.arrivalTime = arrival;
        this.costReg = costReg;
        this.costFirstClass = costFirstClass;
        this.crewList = crewList;
    }

    public Flight() {}
    /*
    * Getters and Setters
    */

    public ArrayList<FlightAttendant> getCrewList(){
        return crewList;
    }

    public String getFlightID(){
        return flightID;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public LocalDateTime getDepartTime() {
        return departTime;
    }

    public void setDepartTime(LocalDateTime time) {
        departTime = time;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime time) {
        arrivalTime = time;
    }

    public int getCostReg() {
        return costReg;
    }

    public void setCostReg(int price) {
        costReg = price;
    }

    public int getCostFirstClass() {
        return costFirstClass;
    }

    public void setCostFirstClass(int price) {
        costFirstClass = price;
    }

    public String FlightInfo() {
        /*
         * return a string of all relevant flight info
         */

        return "FLIGHT";
    }

    public ArrayList<Seat> seatMap() {
        /*
         * grab seat map from plane
         */

        return plane.getSeatMap();
    }
}