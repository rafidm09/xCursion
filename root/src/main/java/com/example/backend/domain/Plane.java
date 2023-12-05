package com.example.backend.domain;

import java.util.ArrayList;

public class Plane {
    private int planeID;
    private int crewSize;
    private String model;
    private String status;
    private ArrayList<Seat> seatMap;

    public Plane(int planeID, int crewSize, String model, String status, ArrayList<Seat> seats) {
        this.planeID = planeID;
        this.crewSize = crewSize;
        this.model = model;
        this.status = status;
        this.seatMap = seats; 
    }

    public int getPlaneID() {
        return planeID;
    }

    public void setPlaneID(int id) {
        planeID = id;
    }

    public int getCrewSize() {
        return crewSize;
    }

    public void setCrewSize(int crewSize) {
        this.crewSize = crewSize;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Seat> getSeatMap() {
        return seatMap;
    }

    public void setSeatMap(ArrayList<Seat> seatMap) {
        this.seatMap = seatMap;
    }
}