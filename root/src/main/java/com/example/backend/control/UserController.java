package com.example.backend.control;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.backend.domain.Ticket;



public class UserController extends AirlineController {
    
    public ArrayList<Ticket> bookFlightGuest(String flightID, int seatID, String name, String address, String email) {
        // create user in db
        addUser(name, address, email, "Guest", null);

        int userID = getUserIdByEmail(email);
        addTicket(userID, flightID, seatID);
        ArrayList<Ticket> tickets = retrieveUserTickets(userID);
        Ticket ticket = new Ticket();
        for (Ticket t : tickets) {
            if (t.getFlight().getFlightID().equals(flightID) && t.getSeat().getSeatID() == seatID) {
                ticket = t;
                break;
            }
        }
        emailTicket(ticket, name, email);
        return tickets;
    }

    public void bookFlightRegistered(String flightID, int seatID, int userID) {
        addTicket(userID, flightID, seatID);
        ArrayList<Ticket> tickets = retrieveUserTickets(userID);
        Ticket ticket = new Ticket();
        for (Ticket t : tickets) {
            if (t.getFlight().getFlightID().equals(flightID) && t.getSeat().getSeatID() == seatID) {
                ticket = t;
                break;
            }
        }
        String email = "";
        try {
            String query = "SELECT Email FROM Users WHERE UserID = ?";
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                preparedStatement.setInt(1, userID);
                ResultSet resultSet = preparedStatement.executeQuery();
                
                while (resultSet.next()) {
                    email = resultSet.getString("Email");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        emailTicket(ticket, getNamebyUserID(userID), email);
    }

    public void cancelTicket(String email, int ticketId) {
        emailUserCancellation(email, ticketId);
        String deleteTicketQuery = "DELETE FROM Ticket WHERE TicketID = ?";
        try (PreparedStatement statement = myConnection.prepareStatement(deleteTicketQuery)) {
            statement.setInt(1, ticketId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert a new ticket
    public void addTicket(int userID, String flightID, int seatID) {
        String insertTicketQuery = "INSERT INTO Ticket (UserID, FlightID, SeatID) VALUES (?, ?, ?)" +
                                    "ON DUPLICATE KEY UPDATE UserID = VALUES(UserID), " +
                                    "FlightID = VALUES(FlightID), SeatID = VALUES(SeatID)";
        try (PreparedStatement statement = myConnection.prepareStatement(insertTicketQuery)) {
            statement.setInt(1, userID);
            statement.setString(2, flightID);
            statement.setInt(3, seatID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
