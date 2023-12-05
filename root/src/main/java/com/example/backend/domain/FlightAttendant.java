package com.example.backend.domain;

//import java.util.ArrayList;

public class FlightAttendant {


    private String name;
    private int flightAttendantID;
    private int UserID;
    private int crewID;


    public FlightAttendant(String name, int flightAttendantID, int userID, int crewID){
        this.name = name;
        this.flightAttendantID = flightAttendantID;
        this.UserID = userID;
        this.crewID = crewID;
    }

    public String getName(){
        return name;
    }
    
    public int getFlightAttendantID() {
        return flightAttendantID;
    }

    public int getUserID(){
        return UserID;
    }

    public int getFlightCrewID(){
        return crewID;
    }

    
}