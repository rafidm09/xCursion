package com.example.backend.domain;

public class Seat {
    private int seatID;
    private int rowNum;
    private String seatPos;
    private String type;

    public Seat(int seatID, int rowNum, String seatPos, String type) {
        this.seatID = seatID;
        this.rowNum = rowNum;
        this.seatPos = seatPos;
        this.type = type;
    }

    public Seat() {}
    /*
    * Getters and Setters
    */
    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int id) {
        seatID = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int row) {
        rowNum = row;
    }

    public String getSeatPos() {
        return seatPos;
    }

    public void setSeatPos(String pos) {
        seatPos = pos;
    }

    // Method to concatenate rowNum and seatPos to return a seat code (i.e. 20A)
    public String getSeatCode() {
        return Integer.toString(rowNum) + seatPos;
    }
}

