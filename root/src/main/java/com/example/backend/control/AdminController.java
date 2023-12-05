package com.example.backend.control;

import java.sql.*;
import java.time.LocalDateTime;

import java.util.ArrayList;

import com.example.backend.domain.*;

public class AdminController extends AirlineController { 

    public ArrayList<Plane> retrieveAllPlanes() {
        ArrayList<Plane> planes = new ArrayList<>();
        try {
            String query = "SELECT * FROM Plane";
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int planeID = resultSet.getInt("PlaneID");
                    String planeType = resultSet.getString("PlaneType");
                    int crewSize = resultSet.getInt("CrewSize");
                    String status = resultSet.getString("AvailabilityStatus");

                    ArrayList<Seat> seatMap = retrieveAllSeats(planeID);
                    Plane plane = new Plane(planeID, crewSize, planeType, status, seatMap);
                    planes.add(plane);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return planes;
    }

    public ArrayList<Seat> retrieveAllSeats(int planeID) {
        ArrayList<Seat> seats = new ArrayList<>();
        try {
            String query = "SELECT * FROM Seat WHERE PlaneID = ?";
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                preparedStatement.setInt(1, planeID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int seatID = resultSet.getInt("SeatId");
                    int rowNum = resultSet.getInt("RowNumber");
                    String seatPos = resultSet.getString("SeatPosition");
                    String type = resultSet.getString("SeatType");
                    Seat seat = new Seat(seatID, rowNum, seatPos, type);
                    seats.add(seat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public ArrayList<FlightAttendant> retrieveAllFlightAttendants(){
        ArrayList<FlightAttendant> allFlightAttendants = new ArrayList<>();
        try {
            String query = "SELECT * FROM FlightAttendant";
            PreparedStatement preparedStatement = myConnection.prepareStatement(query); 
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int flightAttendantID = resultSet.getInt("FlightAttendantID");
                int userID = resultSet.getInt("UserID");
                int crewID = resultSet.getInt("FlightCrewID");
                String name = getNamebyUserID(userID);
                FlightAttendant flightAttendant = new FlightAttendant(name, flightAttendantID, userID, crewID);
                allFlightAttendants.add(flightAttendant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allFlightAttendants;
    }



    public ArrayList<User> retrieveRegisteredUsers() {
        ArrayList<User> registeredUsers = new ArrayList<User>();

        try {
            String query = "SELECT * FROM Users WHERE UserType = 'Registered'";
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int userID = resultSet.getInt("UserId");
                    String name = resultSet.getString("UserName");
                    String email = resultSet.getString("Email");

                    ArrayList<Ticket> tickets = retrieveUserTickets(userID);
                    User user = new User(userID, name, email, "Registered", tickets);
                    registeredUsers.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registeredUsers;
    }



    // void removeFlight(String flightID)

    public void removeFlight(String flightID) {
        try {
            ArrayList<User> userList = retrieveAllUsers();

            for (User user : userList) {
                ArrayList<Ticket> ticketList = user.getTickets();
                for (Ticket t : ticketList) {
                    if (t.getFlight().getFlightID().equals(flightID)) {
                        // Notify user by email
                        emailAdminCancellation(user.getEmail(), t.getTicketID());
                    }
                }
            }

            // Delete all tickets related to flightID
            String query = "DELETE FROM Ticket WHERE FlightID = ?";
            PreparedStatement statement = myConnection.prepareStatement(query);
            statement.setString(1, flightID);
            statement.executeUpdate();

            // Delete flight
            query = "DELETE FROM Flight WHERE FlightID = ?";
            statement = myConnection.prepareStatement(query);
            statement.setString(1, flightID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // void addFlight(String departTime, String arrivalTime, String origin, String dest, String costRegular,
    //     String costBusiness, String flightnumber, String planeID, String crewID)
    public void addFlight(String flightnumber, String departTime, String arrivalTime, String origin, String dest, String costRegular,
            String costBusiness, String planeID, String crewID) {
        try {
            String query = "INSERT INTO Flight (FlightID, DepartTime, ArrivalTime, Origin, Destination, CostRegular, CostBusiness, PlaneID, FlightCrewID)"    
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insert;
            insert = myConnection.prepareStatement(query);
            insert.setString(1, flightnumber);
            insert.setString(2, departTime);
            insert.setString(3, arrivalTime);
            insert.setString(4, origin);
            insert.setString(5, dest);
            insert.setInt(6, Integer.parseInt(costRegular));
            insert.setInt(7, Integer.parseInt(costBusiness));
            insert.setInt(8, Integer.parseInt(planeID));
            insert.setInt(9, Integer.parseInt(crewID));
            insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // void updateFlightInfo(String flightID, String columnName, Object newValue)
    public void updateFlightInfo(String flightID, String columnName, Object newValue) {
        String updateFlightInfoQuery = "UPDATE Flight SET " + columnName + " = ? WHERE FlightID = ?";
        try (PreparedStatement statement = myConnection.prepareStatement(updateFlightInfoQuery)) {
            // Set the new value based on the data type of the column
            if (newValue instanceof String) {
                statement.setString(1, (String) newValue);
            } else {
                statement.setInt(1, (Integer) newValue);
            }
            statement.setString(2, flightID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // void removePlane(int planeID)
    public void removePlane(int planeID) {
        try {
            // Delete all seats related to planeID
            String query = "DELETE FROM Seat WHERE PlaneID = ?";
            PreparedStatement statement = myConnection.prepareStatement(query);
            statement.setInt(1, planeID);
            statement.executeUpdate();

            // Delete plane
            query = "DELETE FROM Plane WHERE PlaneID = ?";
            statement = myConnection.prepareStatement(query);
            statement.setInt(1, planeID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // void addPlane(int planeID, String model, int crewSize, String status)
    public void addPlane(int planeID, String model, int crewSize, String status) {
        try {
            String query = "INSERT INTO Plane (PlaneID, PlaneType, CrewSize, AvailabilityStatus)"
                    + "VALUES(?, ?, ?, ?)"+
                    "ON DUPLICATE KEY UPDATE PlaneType = VALUES(PlaneType), " +
                    "CrewSize = VALUES(CrewSize), AvailabilityStatus = VALUES(AvailabilityStatus)";
            PreparedStatement insert;
            insert = myConnection.prepareStatement(query);
            insert.setInt(1, planeID);
            insert.setString(2, model);
            insert.setInt(3,  crewSize);
            insert.setString(4, status);
                    
            insert.executeUpdate();
            insert.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // void removeCrew(int crewID)
    public void removeCrew(int crewID){

        int[] userIDs = new int[4]; 

        try {
            // updating flightcrewID to null in the db 
            String updateFlightquery = "UPDATE Flight SET FlightCrewID = null WHERE FlightCrewID = ?";
            PreparedStatement preparedStatement = myConnection.prepareStatement(updateFlightquery); 
            preparedStatement.setInt(1, crewID);
            preparedStatement.executeUpdate();

            // finding userID for each crew member and deleting the user 
            for(int i = 0; i < userIDs.length; i++) {
                String query = "SELECT UserID FROM FlightAttendant WHERE FlightCrewID = ?;";
                PreparedStatement preparedStatement2 = myConnection.prepareStatement(query);
                preparedStatement2.setInt(1, crewID);
                ResultSet resultSet = preparedStatement2.executeQuery();
                while (resultSet.next()) {
                    userIDs[i] = resultSet.getInt("UserID");
                }
                String query3 = "DELETE FROM Users WHERE UserID = ?";
                PreparedStatement preparedStatement3 = myConnection.prepareStatement(query3);
                preparedStatement3.setInt(1, userIDs[i]);
                preparedStatement3.executeUpdate();
            }

            String deleteQuery ="DELETE FROM FlightCrew WHERE FlightCrewID = ?";
            PreparedStatement preparedStatement4 = myConnection.prepareStatement(deleteQuery); 
            preparedStatement4.setInt(1, crewID);
            preparedStatement4.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // void addCrew(int crewID, String crewMember1, String crewMember2, String crewMember3, String crewMember4)
    public void addCrew(int crewID, String crewMember1, String crewMember2, String crewMember3, String crewMember4){

        String[] names = {crewMember1, crewMember2, crewMember3, crewMember4};
        int[] userIDs = new int[4]; 

        for(int i = 0; i < names.length; i++) {

            addUser(names[i], null, null, "FlightAttendant", null);
            try {
                // getting new userid 
                String query = "SELECT UserID FROM Users WHERE UserName = ?;";
                PreparedStatement preparedStatement = myConnection.prepareStatement(query);
                preparedStatement.setString(1, names[i]);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    userIDs[i] = resultSet.getInt("UserID");
                }
            
                // updating flight crew table 
                String query2 = "INSERT INTO FlightCrew (FlightCrewID)"
                                + "VALUES(?)"
                                + "ON DUPLICATE KEY UPDATE FlightCrewID = VALUES(FlightCrewID)";
                PreparedStatement insert2;
                insert2 = myConnection.prepareStatement(query2);
                insert2.setInt(1, crewID);              
                insert2.executeUpdate();
                insert2.close();
                
            
                // updating flight attendant table 
                String query3 = "INSERT INTO FlightAttendant (UserID, FlightCrewID)"
                                + "VALUES(?, ?)"+
                                "ON DUPLICATE KEY UPDATE UserID = VALUES(UserID)," +
                                "FlightCrewID = VALUES(FlightCrewID)";
                PreparedStatement insert3;
                insert3 = myConnection.prepareStatement(query3);
                insert3.setInt(1, userIDs[i]);
                insert3.setInt(2, crewID);
                insert3.executeUpdate();
                insert3.close();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ArrayList<User> retrieveAllUsers()
    public ArrayList<User> retrieveAllUsers() {
        ArrayList<User> users = new ArrayList<User>();

        try {
            String query = "SELECT * FROM Users";
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int userID = resultSet.getInt("UserId");
                    String name = resultSet.getString("UserName");
                    String email = resultSet.getString("Email");
                    String type = resultSet.getString("UserType");

                    ArrayList<Ticket> tickets = retrieveUserTickets(userID);
                    User user = new User(userID, name, email, type, tickets);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


    public void updateAllFlightInfo(String flightID, String origin, String dest, LocalDateTime depart, LocalDateTime arrival, int newCostReg, int newCostBusiness, int crewID, int planeID)
    {
        updateFlightInfo(flightID, "Origin", origin);
        updateFlightInfo(flightID, "Destination", dest);
        updateFlightInfo(flightID, "DepartTime", depart.toString());
        updateFlightInfo(flightID, "ArrivalTime", arrival.toString());
        updateFlightInfo(flightID, "CostRegular", newCostReg);
        updateFlightInfo(flightID, "CostBusiness", newCostBusiness);
        updateFlightInfo(flightID, "FlightCrewID", crewID);
        updateFlightInfo(flightID, "PlaneID", planeID);
    }
}






