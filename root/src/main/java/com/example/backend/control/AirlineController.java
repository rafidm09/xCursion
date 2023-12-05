package com.example.backend.control;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import com.example.backend.domain.Flight;
import com.example.backend.domain.FlightAttendant;
import com.example.backend.domain.Plane;
import com.example.backend.domain.Seat;
import com.example.backend.domain.Ticket;
import com.example.backend.domain.User;

public class AirlineController {
    protected static Database db = Database.getInstance();
    protected static Connection myConnection = db.getConnection();

    public boolean validateEmailPassword(String email, String password) {
        try {
            String query = "SELECT PWD FROM Users WHERE Email = ?";
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();
                String pwd_db = "";
                 while (resultSet.next()) {
                    pwd_db = resultSet.getString("pwd");
                }
                return (password.equals(pwd_db)) ? true : false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User login(String email, String password) {
        if (validateEmailPassword(email, password)) {
            int id = 0;
            String name = "", type = "";
            try {
                PreparedStatement create = myConnection.prepareStatement("SELECT UserID, UserName, UserType FROM Users WHERE Email = ?");      
                create.setString(1, email);
                ResultSet resultSet = create.executeQuery();
                while (resultSet.next()) {
                    id = resultSet.getInt("UserID");
                    name = resultSet.getString("UserName");
                    type = resultSet.getString("UserType");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            User user = new User(id, name, email, type, retrieveUserTickets(id));
            return user;
        } else {
            return null;
        }
    }

    public boolean addUser(String username, String address, String email, String UserType, String pwd) {
        ResultSet temp = null;
        PreparedStatement create = null;
        // Check if username exists
        try {
            create = myConnection.prepareStatement("SELECT * FROM Users WHERE Username=?");
            create.setString(1, username);
            temp = create.executeQuery();
            while (temp.next()) {
                return false;
            }

            // Add it to the database
            create = myConnection.prepareStatement("INSERT INTO Users (UserName, Address, Email, UserType, pwd)"
                    + "VALUES(?, ?, ?, ?, ?)");
            create.setString(1, username);
            create.setString(2, address);
            create.setString(3, email);
            create.setString(4, UserType);
            create.setString(5, pwd);
            create.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getNamebyUserID(int userID) {
        String name = "";
        try {
            String query = "SELECT UserName FROM Users WHERE UserID = ?;";
           
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                preparedStatement.setInt(1, userID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    name = resultSet.getString("UserName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }
        return name;
    }

    // Method to find user ID based on email
    public int getUserIdByEmail(String email) {
        String findUserIdQuery = "SELECT UserID FROM Users WHERE Email = ?";
        try (PreparedStatement statement = myConnection.prepareStatement(findUserIdQuery)) {
            statement.setString(1, email);
            // Assuming only one user with the given email
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt("UserID") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
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

    public Plane retrievePlane(int planeID) {
        Plane plane = null;
        try {
            String query = "SELECT * FROM Plane WHERE PlaneID = ?";
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                preparedStatement.setInt(1, planeID);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int crewSize = resultSet.getInt("CrewSize");
                    String model = resultSet.getString("PlaneType");
                    String status = resultSet.getString("AvailabilityStatus");
                    // Retrieve seat information for this aircraft if needed

                    // Create a new Plane object using the retrieved information
                    ArrayList<Seat> seats = retrieveAllSeats(planeID);
                    plane = new Plane(planeID, crewSize, model, status, seats);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plane;
    }

    public ArrayList<Flight> retrieveAllFlights() {
        ArrayList<Flight> flights = new ArrayList<>();
        try {
            String query = "SELECT * FROM Flight";
            PreparedStatement preparedStatement = myConnection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String flightID = resultSet.getString("FlightID");
                String departure_s = resultSet.getString("DepartTime");
                String arrival_s = resultSet.getString("ArrivalTime");
                String origin = resultSet.getString("Origin");
                String destination = resultSet.getString("Destination");
                int costReg = resultSet.getInt("CostRegular");
                int costBusiness = resultSet.getInt("CostBusiness");
                int planeID = resultSet.getInt("PlaneID");
                
                ArrayList<FlightAttendant> crewList = retrieveFlightCrew(flightID);

                LocalDateTime arrival = LocalDateTime.parse(arrival_s);
                LocalDateTime departure = LocalDateTime.parse(departure_s);

                Plane plane = retrievePlane(planeID);
                Flight flight = new Flight(flightID, origin, destination, plane, departure, arrival, costReg, costBusiness, crewList);
                flights.add(flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    public ArrayList<Ticket> retrieveUserTickets(int userID) {
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();

        try {
            String query = "SELECT * FROM Ticket WHERE UserID = ?";
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                preparedStatement.setInt(1, userID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int ticketID = resultSet.getInt("TicketID");
                    String flightID = resultSet.getString("FlightID");
                    int seatID = resultSet.getInt("SeatID");

                    // Get list of flights to get single Flight object with matching FlightID
                    ArrayList<Flight> flightList = retrieveAllFlights();
                    Flight flight = new Flight();
                    for (Flight f : flightList) {
                        if (f.getFlightID().equals(flightID)) {
                            flight = f;
                            break;
                        }
                    }

                    // Get list of seats to get single Seat object with matching seatID
                    ArrayList<Seat> seatList = retrieveAllSeats(flight.getPlane().getPlaneID());
                    Seat seat = new Seat();
                    for (Seat s : seatList) {
                        if (s.getSeatID() == seatID) {
                            seat = s;
                            break;
                        }
                    }
                    Ticket t = new Ticket(ticketID, flight, seat);
                    tickets.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }
        return tickets;
    }

    public ArrayList<Ticket> retrieveAllTickets() {
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();

        try {
            String query = "SELECT * FROM Ticket";
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int ticketID = resultSet.getInt("TicketID");
                    String flightID = resultSet.getString("FlightID");
                    int seatID = resultSet.getInt("SeatID");

                    // Get list of flights to get single Flight object with matching FlightID
                    ArrayList<Flight> flightList = retrieveAllFlights();
                    Flight flight = new Flight();
                    for (Flight f : flightList) {
                        if (f.getFlightID().equals(flightID)) {
                            flight = f;
                            break;
                        }
                    }
                    // Get list of seats to get single Seat object with matching seatID
                    ArrayList<Seat> seatList = retrieveAllSeats(flight.getPlane().getPlaneID());
                    Seat seat = new Seat();
                    for (Seat s : seatList) {
                        if (s.getSeatID() == seatID) {
                            seat = s;
                            break;
                        }
                    }
                    Ticket t = new Ticket(ticketID, flight, seat);
                    tickets.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        }
        return tickets;
    }


    public ArrayList<FlightAttendant> retrieveFlightCrew(String flightID){
        ArrayList<FlightAttendant> crewList = new ArrayList<>();
        try {
            String query =  "SELECT *" +
                            "FROM FlightAttendant " +
                            "WHERE FlightCrewID = "+ 
                            "(SELECT FlightCrewID FROM Flight WHERE FlightID = ?);";
           
            try (PreparedStatement preparedStatement = myConnection.prepareStatement(query)) {
                preparedStatement.setString(1, flightID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int flightAttendantID = resultSet.getInt("FlightAttendantID");
                    int userID = resultSet.getInt("UserID");
                    int crewID = resultSet.getInt("FlightCrewID");
                    String name = getNamebyUserID(userID);
                    FlightAttendant flightAttendant = new FlightAttendant(name, flightAttendantID, userID, crewID);

                    crewList.add(flightAttendant);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crewList;
    }

    public void emailTicket(Ticket ticket, String name, String email) {
        // Recipient's email ID needs to be mentioned.

        // Sender's email ID needs to be mentioned
        String from = "web@gmail.com";

        // Assuming you are sending email through Gmail
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.port", "587");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Replace with your Gmail address and password
                return new PasswordAuthentication("air480flights@gmail.com", "zeva bkjb hqhf enqi");
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            // Set Subject: header field
            message.setSubject("Flight Purchase Confirmation");

            Flight flight = ticket.getFlight();
            Seat seat = ticket.getSeat();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM d, h:mm a");
            String departure = flight.getDepartTime().format(formatter);
            String arrival = flight.getArrivalTime().format(formatter);
        
            double total = (double) (seat.getType().equals("Business") ? flight.getCostFirstClass() : flight.getCostReg());
            String formattedTotal = String.format("%.2f", total);
            String cost = String.format("%.2f", total * 0.6);
            String tax = String.format("%.2f", total * 0.4);
            Duration duration = Duration.between(flight.getDepartTime(), flight.getArrivalTime());
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();

            // Set body of email
            message.setText("Thank you, " + name + "! Your flight is booked.\n\n"
                            + flight.getOrigin() + " to " + flight.getDestination()
                            + "\n" + flight.getFlightID() + "\n" + seat.getType()
                            + "\n" + departure + " - " + arrival 
                            + "\n" + hours + "h " + minutes + "m flight duration"
                            + "\n\n" + "Price Summary\n"
                            + "1 traveller\t\t\t\t       CA $" + cost + "\n"  
                            + "Taxes and Fees\t\t\t\tCA $" + tax + "\n\n"
                            + "Total\t\t\t\t\t\t CA $" + formattedTotal);      
            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


    public void emailUserCancellation(String email, int ticketId) {
        // Find ticket that is being cancelled
        ArrayList<Ticket> ticketList = retrieveAllTickets();
        Ticket ticket = new Ticket();
        for (Ticket t : ticketList) {
            if (t.getTicketID() == ticketId) {
                ticket = t;
                break;
            }
        }

        String from = "web@gmail.com";
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.port", "587");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Replace with your Gmail address and password
                return new PasswordAuthentication("air480flights@gmail.com", "zeva bkjb hqhf enqi");
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            // Set Subject: header field
            message.setSubject("Flight Cancellation Notification");

            Flight flight = ticket.getFlight();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM d, h:mm a");
            String departure = flight.getDepartTime().format(formatter);
            String arrival = flight.getArrivalTime().format(formatter);

            // Set body of email
            message.setText("You have successfully cancelled your flight.\n\n"
                            + flight.getOrigin() + " to " + flight.getDestination()
                            + "\n" + departure + " - " + arrival);
            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


    public void emailAdminCancellation(String email, int ticketId) {
        // Find ticket that is being cancelled
        ArrayList<Ticket> ticketList = retrieveAllTickets();
        Ticket ticket = new Ticket();
        for (Ticket t : ticketList) {
            if (t.getTicketID() == ticketId) {
                ticket = t;
                break;
            }
        }

        String from = "web@gmail.com";
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.port", "587");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Replace with your Gmail address and password
                return new PasswordAuthentication("air480flights@gmail.com", "zeva bkjb hqhf enqi");
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            // Set Subject: header field
            message.setSubject("Flight Cancellation Notification");

            Flight flight = ticket.getFlight();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM d, h:mm a");
            String departure = flight.getDepartTime().format(formatter);
            String arrival = flight.getArrivalTime().format(formatter);

            // Set body of email
            message.setText("We regret to inform you that your flight has been cancelled.\n\n"
                            + flight.getOrigin() + " to " + flight.getDestination()
                            + "\n" + departure + " - " + arrival + "\n\n"
                            + "You will receive a full refund.");
            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}

