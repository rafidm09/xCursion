package com.example.backend.root;

import com.example.backend.control.*;
import com.example.backend.domain.Flight;
import com.example.backend.domain.FlightAttendant;
import com.example.backend.domain.Plane;
import com.example.backend.domain.Ticket;
import com.example.backend.domain.User;

import jakarta.websocket.server.PathParam;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RootApplication {

	private AirlineController airline = new AirlineController();
	private AdminController admin = new AdminController();
	private UserController user = new UserController();

	public static void main(String[] args) {
		SpringApplication.run(RootApplication.class, args);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/login")
	public ArrayList<User> login(@PathParam("email") String email, @PathParam("password") String password) {
		ArrayList<User> t = new ArrayList<>();
		t.add(airline.login(email, password));
		return t;
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/register")
	public ArrayList<User> register(@PathParam("email") String email, @PathParam("password") String password,
			@PathParam("name") String name, @PathParam("addr") String addr) {
		if (airline.addUser(name, addr, email, "Registered", password)) {
			return login(email, password);
		}
		return null;

	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/getFlightInfo")
	/**
	 * Creates instance of Database and calls retrieveAllFlights()
	 * 
	 * @return ArrayList of Flight from database and returns it to front end
	 */
	public ArrayList<Flight> getFlightInfo() {
		ArrayList<Flight> flights = airline.retrieveAllFlights();

		return flights;
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/bookTicket")
	public ArrayList<Ticket> bookTicket(@RequestParam("fid") String fId, @RequestParam("sid") String sId,
			@RequestParam("name") String name,
			@RequestParam("email") String email) {
		return user.bookFlightGuest(fId, Integer.parseInt(sId), name, "123 ave", email);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/bookTicket2")
	public void bookTicketUser(@RequestParam("fid") String fId, @RequestParam("sid") String sId,
			@RequestParam("name") String uId) {
		user.bookFlightRegistered(fId, Integer.parseInt(sId), Integer.parseInt(uId));
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/getAllTickets")
	public ArrayList<Ticket> getAllTickets() {
		return airline.retrieveAllTickets();
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/cancelTicket")
	public void cancelTicket(@RequestParam("email") String email, @RequestParam("tid") String tId) {
		user.cancelTicket(email, Integer.parseInt(tId));

	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/getUserTickets")
	public ArrayList<Ticket> getUserTickets(@RequestParam("uId") String uId) {
		return airline.retrieveUserTickets(Integer.parseInt(uId));
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/getUsers")
	public ArrayList<User> getAllPassengers() {
		return admin.retrieveAllUsers();
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/updateAllFlightInfo")
	public void updateAllFlightInfo(@RequestParam("fid") String flightID, @RequestParam("origin") String origin,
			@RequestParam("dest") String dest, @RequestParam("depart") String depart,
			@RequestParam("arrival") String arrival, @RequestParam("newcostreg") int newCostReg,
			@RequestParam("newcostbusiness") int newCostBusiness, @RequestParam("cid") int crewID,
			@RequestParam("pid") int planeID) {
		System.out.println("TEST: " + dest);
		admin.updateAllFlightInfo(flightID, origin, dest, LocalDateTime.parse(depart), LocalDateTime.parse(arrival),
				newCostReg, newCostBusiness, crewID, planeID);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/addPlane")
	public void addPlane(@RequestParam("pid") int planeID, @RequestParam("model") String model,
			@RequestParam("crewsize") int crewSize, @RequestParam("status") String status) {
		admin.addPlane(planeID, model, crewSize, status);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/removePlane")
	public void removePlane(@RequestParam("pid") int planeID) {
		admin.removePlane(planeID);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/addCrew")
	public void addCrew(@RequestParam("cid") int crewID, @RequestParam("name1") String name1,
			@RequestParam("name2") String name2, @RequestParam("name3") String name3,
			@RequestParam("name4") String name4) {
		admin.addCrew(crewID, name1, name2, name3, name4);
		;
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/removeCrew")
	public void removeCrew(@RequestParam("cid") int crewID) {
		admin.removeCrew(crewID);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/addFlight")
	public void addFlight(@RequestParam("fid") String flightID, @RequestParam("depart") String departTime,
			@RequestParam("arrival") String arrivalTime, @RequestParam("origin") String origin,
			@RequestParam("dest") String dest, @RequestParam("costregular") String costRegular,
			@RequestParam("costbusiness") String costBusiness, @RequestParam("pid") String planeID,
			@RequestParam("cid") String crewID) {
		admin.addFlight(flightID, departTime, arrivalTime, origin, dest, costRegular, costBusiness, planeID, crewID);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/removeFlight")
	public void removeFlight(@RequestParam("fid") String flightID) {
		admin.removeFlight(flightID);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/getAllCrews")
	public ArrayList<FlightAttendant> getAllCrews() {
		return admin.retrieveAllFlightAttendants();
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/api/getAllPlanes")
	public ArrayList<Plane> getAllPlanes() {
		return admin.retrieveAllPlanes();
	}
}