import { useEffect, useState } from "react";

const BuyerWin = ({
    flight,
    seats,
    user,
    booked,
    setBookedTickets,
    setGuestEmail,
}) => {
    //console.log("flight", flight);
    //console.log("seat", seats);
    const [showPopup, setShowPopup] = useState(false);
    //console.log(seats);
    const handleBuyNow = () => {
        setShowPopup(true);
    };

    if (flight === null) {
        return;
    }

    const handlePayment = async (paymentInfo) => {
        //console.log(paymentInfo);
        setShowPopup(false);
        if (!paymentInfo) {
            return;
        }

        if (user) {
            await fetch(
                `http://localhost:8080/api/bookTicket2?fid=${flight.flightID}&sid=${seats[0].seatID}&name=${user.userID}`,
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
                })
                .catch((error) => {
                    console.error("Error:", error);
                });
        } else {
            await fetch(
                `http://localhost:8080/api/bookTicket?fid=${flight.flightID}&sid=${seats[0].seatID}&name=${paymentInfo.name}&email=${paymentInfo.email}`,
                {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            )
                .then((response) => response.json())
                .then((data) => {
                    //console.log("SUBMITTED", data);
                    setBookedTickets((prev) => [...prev, data]);
                    setGuestEmail(paymentInfo.email);
                })
                .catch((error) => {
                    console.error("Error:", error);
                });
        }
        booked();
    };

    const formatDate = (d) => {
        const date = new Date(d);

        const formattedDate = `${date.getDate()}-${
            date.getMonth() + 1
        }-${date.getFullYear()} ${date.getHours()}:${date.getMinutes()}`;

        return formattedDate;
    };

    //console.log("test", seats);

    const calcCost = () => {
        let total = 0;
        seats.map((s, index) => {
            if (s.type === "Business") {
                total += flight.costFirstClass;
            } else {
                total += flight.costReg;
            }
        });

        return total;
    };

    return (
        <div>
            <h2 className="flight-name-h2">Flight {flight.flightID}</h2>
            <h3 id="a-to-b">
                {flight.origin} &rarr; {flight.destination}
            </h3>
            <div className="flight-info">
                <h3>Departure: {formatDate(flight.departTime)} </h3>
                <h3>ETA: {formatDate(flight.arrivalTime)}</h3>
            </div>
            <div className="price-container">
                <h6>Seat - Type - Price</h6>
                <div id="scrollable">
                    {seats.map((s, index) => (
                        <h4 key={index}>
                            {s.seatCode} -{" "}
                            {s.type === "Business" ? "First Class" : "Regular"}{" "}
                            - $
                            {s.type === "Business"
                                ? flight.costFirstClass
                                : flight.costReg}
                        </h4>
                    ))}
                </div>
                <h4>Total Cost: ${calcCost()}</h4>
            </div>
            <button className="buy-btn" onClick={handleBuyNow}>
                Buy Now
            </button>
            <div>
                {/* ... existing code ... */}
                {showPopup && <div className="backdrop" />}
                {showPopup && (
                    <Popup
                        onPayment={handlePayment}
                        cost={calcCost()}
                        user={user}
                    />
                )}
            </div>
        </div>
    );
};

const Popup = ({ onPayment, cost, user }) => {
    const [name, setName] = useState(user ? user.name : "");
    const [email, setEmail] = useState(user ? user.email : "");
    const [cardNumber, setCardNumber] = useState(
        user ? "2134 1231 1231 1231" : ""
    );
    const [expiryDate, setExpiryDate] = useState(user ? "02/2027" : "");
    const [cvv, setCvv] = useState(user ? "354" : "");

    const handleSubmit = (event) => {
        event.preventDefault();
        const paymentInfo = { name, cardNumber, expiryDate, cvv, email };
        onPayment(paymentInfo);
    };

    return (
        <form onSubmit={handleSubmit} className="popup">
            <button
                type="button"
                onClick={() => onPayment(null)}
                className="close-btn"
            >
                X
            </button>
            <h3>Total - ${cost}</h3>
            <label>
                Name:
                <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder={user ? user.name : "name"}
                    required
                />
            </label>
            <label>
                Email:
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
            </label>
            <label>
                Card Number:
                <input
                    type="text"
                    value={cardNumber}
                    onChange={(e) => setCardNumber(e.target.value)}
                    required
                />
            </label>
            <div className="split">
                <label>
                    Expiry Date:
                    <input
                        type="text"
                        value={expiryDate}
                        onChange={(e) => setExpiryDate(e.target.value)}
                        required
                    />
                </label>
                <label>
                    CVV:
                    <input
                        type="text"
                        value={cvv}
                        onChange={(e) => setCvv(e.target.value)}
                        required
                    />
                </label>
            </div>
            <button type="submit">Submit</button>
        </form>
    );
};

export default BuyerWin;
