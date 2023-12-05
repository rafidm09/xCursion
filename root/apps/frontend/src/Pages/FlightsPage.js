import { useEffect, useState } from "react";
import SeatGrid from "../Components/SeatGrid";
import BuyerWin from "../Components/BuyerWin";
import ManageFlightsPage from "../Components/ManageFlightsPage";
import { useNavigate } from "react-router-dom";

const FlightsPage = ({ user, setUser, setBookedTickets, setGuestEmail }) => {
    const [selectedSeats, setSelectedSeats] = useState([]);
    //const [userType, setUserType] = useState(user ? user.UserType : "none");

    //console.log(generateRandomLetters(4)); // Outputs: Random 4 letters

    const [sortField, setSortField] = useState(null);
    const [sortDirection, setSortDirection] = useState("asc");

    const formatDate = (d) => {
        const date = new Date(d);
        const formattedDate = `${date.getDate()}-${
            date.getMonth() + 1
        }-${date.getFullYear()} ${date.getHours()}:${date.getMinutes()}`;
        return formattedDate;
    };

    const [flights, setFlights] = useState([]);
    const [tickets, setTickets] = useState([]);
    const nav = useNavigate();

    //console.log(selectedSeats.length);

    useState(() => {
        if (tickets.length === 0) {
            fetch("http://localhost:8080/api/getAllTickets", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            })
                .then((response) => response.json())
                .then((data) => setTickets(data))
                .catch((error) => {
                    console.error("Error:", error);
                });
        }
    }, [tickets]);

    const booked = async () => {
        await fetch("http://localhost:8080/api/getAllTickets", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((response) => response.json())
            .then((data) => setTickets(data))
            .catch((error) => {
                console.error("Error:", error);
            });

        setActiveFlight(null);

        nav("/demo/build/tickets");
    };

    const [res, setRes] = useState([]);
    useEffect(() => {
        if (res.length === 0) {
            getAllFlightInfo();
        }
        //console.log(flights[0]);
        if (flights.length === 0 || flights === null) {
            //setFlights({ error: "101" });
        }
        //console.log(res);
    }, [res]);
    //console.log(flights[0]);

    const getAllFlightInfo = async () => {
        await fetch("http://localhost:8080/api/getFlightInfo")
            .then((response) => response.json())
            .then((data) => setFlights(data))
            .catch((e) => setRes(2));
    };

    const [promoFlightID, setPromoFlightID] = useState(null);

    useEffect(() => {
        if (flights.length > 0) {
            const randomIndex = Math.floor(Math.random() * flights.length);
            setPromoFlightID(flights[randomIndex].flightID);
        }
    }, [flights]);

    const [activeFlight, setActiveFlight] = useState(null);
    const [managingFlight, setManagingFlight] = useState(null);
    const [isListVisible, setIsListVisible] = useState(false);
    const [isBooking, setIsBooking] = useState(false);

    const book = (flight) => {
        setSelectedSeats([]);
        if (activeFlight === flight) {
            setActiveFlight(null);
        } else {
            setIsListVisible(false);
            setActiveFlight(flight);
            //console.log(flight);
        }
    };

    const [viewPassengers, setViewPassengers] = useState([]);

    const displayPassengerList = async (flight) => {
        setActiveFlight(null);

        if (viewPassengers !== flight.Passengers || isListVisible === false) {
            let everyone = [];
            await fetch("http://localhost:8080/api/getUsers", {
                method: "GET",
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
            })
                .then((response) => response.json())
                .then((data) => {
                    //everyone = data; // This will be an array of users
                    for (var i = 0; i < data.length; i++) {
                        for (var x = 0; x < data[i].tickets.length; x++) {
                            if (
                                data[i].tickets.length > 0 &&
                                data[i].tickets[x].flight.flightID ===
                                    flight.flightID
                            ) {
                                everyone.push({
                                    name: data[i].name,
                                    email: data[i].email,
                                });
                            }
                            //console.log("E", everyone);
                        }
                    }
                    setViewPassengers(everyone);
                })
                .catch((error) => {
                    console.error("Error:", error);
                });
            //console.log(everyone);
            setIsListVisible(true);
        } else {
            setIsListVisible(false);
        }
    };

    const [managing, setManaging] = useState(false);

    const manage = (flight) => {
        if (managing && flight === managingFlight) {
            setManaging(false);
        } else {
            setManagingFlight(flight);
            setManaging(true);
        }
    };

    const handleHeaderClick = (field) => {
        setSortField(field);
        setSortDirection(sortDirection === "asc" ? "desc" : "asc");
    };

    const [searchText, setSearchText] = useState("");

    const handleSearchChange = (event) => {
        setSearchText(event.target.value);
    };

    const sortedFlights = [...flights].sort((a, b) => {
        if (a[sortField] < b[sortField]) {
            return sortDirection === "asc" ? -1 : 1;
        }
        if (a[sortField] > b[sortField]) {
            return sortDirection === "asc" ? 1 : -1;
        }
        return 0;
    });

    const filteredFlights = sortedFlights.filter((flight) => {
        return (
            flight.flightID.includes(searchText) ||
            flight.destination.includes(searchText)
        );
    });

    return (
        <div>
            <h1 id="body-title">Flights</h1>
            <input
                style={{
                    margin: "10px 50px",
                    padding: "5px",
                    fontSize: "16px",
                    width: "20%",
                }}
                type="text"
                value={searchText}
                onChange={handleSearchChange}
                placeholder="Search by Flight ID or Destination"
            />
            {user ? (
                <h6
                    style={{
                        backgroundColor: "yellow",
                        width: "fit-content",
                        margin: "10px 50px",
                    }}
                >
                    Promo Flight
                </h6>
            ) : null}

            <div className="content">
                <div className="flights-container">
                    <table>
                        <thead>
                            <tr>
                                <th
                                    onClick={() =>
                                        handleHeaderClick("flightID")
                                    }
                                >
                                    Flight{" "}
                                    {sortField === "flightID" &&
                                        (sortDirection === "asc" ? "▲" : "▼")}
                                </th>
                                <th
                                    onClick={() =>
                                        handleHeaderClick("departTime")
                                    }
                                >
                                    Depart Time{" "}
                                    {sortField === "departTime" &&
                                        (sortDirection === "asc" ? "▲" : "▼")}
                                </th>
                                <th
                                    onClick={() =>
                                        handleHeaderClick("arrivalTime")
                                    }
                                >
                                    ETA{" "}
                                    {sortField === "arrivalTime" &&
                                        (sortDirection === "asc" ? "▲" : "▼")}
                                </th>
                                <th onClick={() => handleHeaderClick("origin")}>
                                    Origin{" "}
                                    {sortField === "origin" &&
                                        (sortDirection === "asc" ? "▲" : "▼")}
                                </th>
                                <th onClick={() => handleHeaderClick("dest")}>
                                    Dest{" "}
                                    {sortField === "dest" &&
                                        (sortDirection === "asc" ? "▲" : "▼")}
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredFlights.map((flight, index) => (
                                <tr
                                    key={index}
                                    style={
                                        flight.flightID === promoFlightID &&
                                        user
                                            ? { backgroundColor: "yellow" }
                                            : {}
                                    }
                                >
                                    <td>{flight.flightID}</td>
                                    <td>{formatDate(flight.departTime)}</td>
                                    <td>{formatDate(flight.arrivalTime)}</td>
                                    <td>{flight.origin}</td>
                                    <td>{flight.destination}</td>
                                    <td>
                                        <button
                                            className="book-btn"
                                            onClick={() => book(flight)}
                                        >
                                            Book Flight
                                        </button>
                                    </td>
                                    {user &&
                                    (user.type == "AirlineAgent" ||
                                        user.type == "FlightAttendant" ||
                                        user.type == "Admin") ? (
                                        <td>
                                            <button
                                                onClick={() =>
                                                    displayPassengerList(flight)
                                                }
                                                className="book-btn"
                                            >
                                                Passenger List
                                            </button>
                                        </td>
                                    ) : null}
                                    {user && user.type == "Admin" ? (
                                        <td>
                                            <button
                                                onClick={() => manage(flight)}
                                                className="book-btn"
                                            >
                                                Manage
                                            </button>
                                        </td>
                                    ) : null}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
                <div className="buyer-container">
                    <div className="grid-container">
                        <SeatGrid
                            flight={activeFlight}
                            selectedSeats={selectedSeats}
                            setSelectedSeats={setSelectedSeats}
                            tickets={tickets}
                        />
                    </div>
                    {selectedSeats.length > 0 && activeFlight ? (
                        <div className="ticket-container">
                            <BuyerWin
                                flight={activeFlight}
                                seats={selectedSeats}
                                user={user}
                                booked={booked}
                                setBookedTickets={setBookedTickets}
                                setGuestEmail={setGuestEmail}
                            />
                        </div>
                    ) : null}
                </div>
                {isListVisible && viewPassengers ? (
                    <div className="passenger-container">
                        <table>
                            <thead>
                                <tr>
                                    <td>Name</td>
                                    <td>Email</td>
                                </tr>
                            </thead>
                            <tbody>
                                {viewPassengers.map((p, index) => (
                                    <tr key={index}>
                                        <td>{p.name}</td>
                                        <td>{p.email}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                ) : null}

                {managing ? (
                    <div>
                        <ManageFlightsPage
                            flight={managingFlight}
                            getAllFlightInfo={getAllFlightInfo}
                        />
                    </div>
                ) : null}
            </div>
        </div>
    );
};

export default FlightsPage;
