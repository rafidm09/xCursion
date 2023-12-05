import { useEffect } from "react";

const TicketPage = ({ user, bookedTickets, guestEmail, setBookedTickets }) => {
    //console.log("U", user, "T", bookedTickets);
    // useEffect(() => {
    //     if (user) {
    //         console.log("INI");
    //         setBookedTickets(user.tickets);
    //     }
    // }, []);

    useEffect(() => {
        if (user) {
            fetch(
                "http://localhost:8080/api/getUserTickets?uId=" + user.userID,
                {
                    method: "GET",
                    headers: {
                        Accept: "application/json",
                        "Content-Type": "application/json",
                    },
                }
            )
                .then((response) => response.json())
                .then((data) => {
                    setBookedTickets([data]); // This will be an array of tickets
                })
                .catch((error) => {
                    console.error("Error:", error);
                });
        }
    });

    if (!user && bookedTickets.length === 0) {
        return <h1>Sign in to view your tickets</h1>;
    }
    //console.log("NEW", bookedTickets);

    //console.log("EMAIL", guestEmail);

    const formatDateTime = (dateTimeStr) => {
        const options = {
            year: "numeric",
            month: "long",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        };
        const date = new Date(dateTimeStr);
        return date.toLocaleString(undefined, options);
    };

    //console.log("user", user);

    const cancelTicket = async (ticket) => {
        //console.log("cancel", ticket);
        await fetch(
            `http://localhost:8080/api/cancelTicket?email=${
                user ? user.email : guestEmail
            }&tid=${ticket.ticketID}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }
        )
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }
                //return response.json();
            })
            .catch((error) => console.error("Error:", error));
        //console.log("SUCCESS");
        //console.log("TICKEt", ticket);
        //console.log("BOOKKED", bookedTickets);

        setBookedTickets((prevBookedTickets) =>
            prevBookedTickets.map((ticketArray, index) =>
                index === 0
                    ? ticketArray.filter((t) => t.ticketID !== ticket.ticketID)
                    : ticketArray
            )
        );

        //console.log("BOOKKED", bookedTickets);
    };

    const getTicketPrice = (t) => {
        //console.log(t);
        if (t.seat.type === "Business") {
            return t.flight.costFirstClass;
        } else {
            return t.flight.costReg;
        }
    };

    return (
        <div>
            <h1>Tickets</h1>
            {bookedTickets[0].map((ticket, index) => {
                return (
                    <div key={index} className="ticket-box-full">
                        <div className="ticket-box">
                            <h1>Flight {ticket.flight.flightID}</h1>
                            <h2>
                                {ticket.flight.origin} &rarr;{" "}
                                {ticket.flight.destination}
                            </h2>
                            <h4>Plane: {ticket.flight.plane.model}</h4>
                            <div>
                                <h3>
                                    Departure Time:{" "}
                                    {formatDateTime(ticket.flight.departTime)}
                                </h3>
                                <h3>
                                    ETA:{" "}
                                    {formatDateTime(ticket.flight.arrivalTime)}
                                </h3>
                            </div>
                            <h4>
                                Seat: {ticket.seat.seatCode} -{" "}
                                {ticket.seat.type}
                            </h4>
                        </div>
                        <div className="action-section">
                            <h1>${getTicketPrice(ticket)}</h1>
                            <button
                                className="cancel-btn"
                                onClick={() => cancelTicket(ticket)}
                            >
                                Cancel Ticket
                            </button>
                        </div>
                    </div>
                );
            })}
        </div>
    );
};

export default TicketPage;
