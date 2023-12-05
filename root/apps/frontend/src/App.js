import logo from "./logo.svg";
import "./App.css";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Header from "./Components/Header";
import FlightsPage from "./Pages/FlightsPage";
import ManageFlightsPage from "./Components/ManageFlightsPage";
import { useEffect, useState } from "react";
import Signin from "./Pages/Signin";
import TicketPage from "./Pages/TicketPage";
import ManagerPage from "./Pages/ManagerPage";
import Confirm from "./Pages/Confirm";

function App() {
    const [user, setUser] = useState(null);
    const [bookedTickets, setBookedTickets] = useState([]);
    const [guestEmail, setGuestEmail] = useState("");
    const [allTickets, setAllTickets] = useState([]);

    useEffect(() => {
        if (user) {
            setBookedTickets([user.tickets]);
        }
    }, [user]);

    return (
        <Router>
            <div className="App">
                <Header user={user} setUser={setUser} />
                <Routes>
                    <Route
                        path="/demo/build"
                        element={
                            <FlightsPage
                                user={user}
                                setUser={setUser}
                                setBookedTickets={setBookedTickets}
                                setGuestEmail={setGuestEmail}
                            />
                        }
                    />
                    <Route
                        path="/demo/build/tickets"
                        element={
                            <TicketPage
                                user={user}
                                bookedTickets={bookedTickets}
                                guestEmail={guestEmail}
                                setBookedTickets={setBookedTickets}
                            />
                        }
                    />
                    <Route
                        path="/demo/build/signin"
                        element={<Signin user={user} setUser={setUser} />}
                    />
                    <Route
                        path="/demo/build/manage"
                        element={<ManagerPage />}
                    />
                    <Route path="/demo/build/confirm" element={<Confirm />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
