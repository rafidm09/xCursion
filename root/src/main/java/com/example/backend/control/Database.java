package com.example.backend.control;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import com.example.backend.domain.Flight;
import com.example.backend.domain.FlightAttendant;
import com.example.backend.domain.Plane;
import com.example.backend.domain.Seat;
import com.example.backend.domain.Ticket;
import com.example.backend.domain.User;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Database implements ConnectionData {

    // Connection to the db
    private static Database instance = null;
    private Connection myConnection;

    private Database() {
        // Initialize the database connection here
        connectionInitialization();
    }

    public Connection getConnection() {
        return myConnection;
    }

    // Static method to get the singleton instance
    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    public Statement myStatement;

    private void connectionInitialization() {
        try {
            myConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/xcursion", "root", "000spe136731");
            myStatement = myConnection.createStatement();
            System.out.println("Connected to database");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Checks if email is in database and password is correct for corresponding
     * email
     *
     * @param email
     * @param password
     * @return Returns true if email and password are in database and correct, else
     *         false
     */
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
                PreparedStatement create = myConnection
                        .prepareStatement("SELECT UserID, UserName, UserType FROM Users WHERE Email = ?");
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
    }

    /**
     * Creates a user in the database and initializes them with the arguments given
     *
     * @param username
     * @param address
     * @param email
     * @param UserType
     * @param pwd
     * @return Returns true if it was successful, false if an account already exists
     *         with the given username
     */
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

    /**
     * Inserts a flight into the database and initializes it with the given
     * arguments
     *
     * @param DepartTime
     * @param ArrivalTime
     * @param origin
     * @param dest
     * @param CostRegular
     * @param CostBusiness
     * @param flightnumber
     * @param planeID
     * @param FlightCrewID
     */
    public void addFlight(String departTime, String arrivalTime, String origin, String dest, String costRegular,
            String costBusiness, String flightnumber, String planeID, String crewID) {
        try {
            String query = "INSERT INTO Flight (DepartTime, ArrivalTime, Origin, Destination, CostRegular, CostBusiness, FlightID, PlaneID, FlightCrewID)"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insert;
            insert = myConnection.prepareStatement(query);
            insert.setString(1, departTime);
            insert.setString(2, arrivalTime);
            insert.setString(3, origin);
            insert.setString(4, dest);
            insert.setInt(5, Integer.parseInt(costRegular));
            insert.setInt(6, Integer.parseInt(costBusiness));
            insert.setString(7, flightnumber);
            insert.setInt(8, Integer.parseInt(planeID));
            insert.setInt(9, Integer.parseInt(crewID));
            insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update flight information
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
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Flight information updated successfully!");
            } else {
                System.out.println("Flight not found or update failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFlight(String flightID) {
        try {
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

    /**
     * function which creates a plane object
     *
     */
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
                Flight flight = new Flight(flightID, origin, destination, plane, departure, arrival, costReg,
                        costBusiness, crewList);
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

    // Method to remove a plane from database
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

    public void addPlane(int planeID, String model, int crewSize, String status) {
        try {
            String query = "INSERT INTO Plane (PlaneID, PlaneType, CrewSize, AvailabilityStatus)"
                    + "VALUES(?, ?, ?, ?)" +
                    "ON DUPLICATE KEY UPDATE PlaneType = VALUES(PlaneType), " +
                    "CrewSize = VALUES(CrewSize), AvailabilityStatus = VALUES(AvailabilityStatus)";
            PreparedStatement insert;
            insert = myConnection.prepareStatement(query);
            insert.setInt(1, planeID);
            insert.setString(2, model);
            insert.setInt(3, crewSize);
            insert.setString(4, status);

            insert.executeUpdate();
            insert.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public ArrayList<FlightAttendant> retrieveFlightCrew(String flightID) {
        ArrayList<FlightAttendant> crewList = new ArrayList<>();
        try {
            String query = "SELECT *" +
                    "FROM FlightAttendant " +
                    "WHERE FlightCrewID = " +
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

    public ArrayList<FlightAttendant> retrieveAllFlightAttendants() {
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

    public void removeCrew(int crewID) {

        int[] userIDs = new int[4];

        try {
            // updating flightcrewID to null in the db
            String updateFlightquery = "UPDATE Flight SET FlightCrewID = null WHERE FlightCrewID = ?";
            PreparedStatement preparedStatement = myConnection.prepareStatement(updateFlightquery);
            preparedStatement.setInt(1, crewID);
            preparedStatement.executeUpdate();

            // finding userID for each crew member and deleting the user
            for (int i = 0; i < userIDs.length; i++) {
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

            String deleteQuery = "DELETE FROM FlightCrew WHERE FlightCrewID = ?";
            PreparedStatement preparedStatement4 = myConnection.prepareStatement(deleteQuery);
            preparedStatement4.setInt(1, crewID);
            preparedStatement4.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCrew(int crewID, String crewMember1, String crewMember2, String crewMember3, String crewMember4) {

        String[] names = { crewMember1, crewMember2, crewMember3, crewMember4 };
        int[] userIDs = new int[4];

        for (int i = 0; i < names.length; i++) {

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
                        + "VALUES(?, ?)" +
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

    public void cancelTicket(String email, int ticketId) {
        emailCancellation(email, ticketId);
        String deleteTicketQuery = "DELETE FROM Ticket WHERE TicketID = ?";
        try (PreparedStatement statement = myConnection.prepareStatement(deleteTicketQuery)) {
            statement.setInt(1, ticketId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                return new PasswordAuthentication("your-email", "your-app-password");
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

            double total = (double) (seat.getType().equals("Business") ? flight.getCostFirstClass()
                    : flight.getCostReg());
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

    public void emailCancellation(String email, int ticketId) {
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
                return new PasswordAuthentication("spencervr1@gmail.com", "your-app-password");
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
            message.setText("Your flight has been cancelled.\n\n"
                    + flight.getOrigin() + " to " + flight.getDestination()
                    + "\n" + departure + " - " + arrival);
            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Database db = Database.getInstance();

        db.addTicket(1, "A98", 26);
    }
}
