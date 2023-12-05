import { useState } from "react";

const SeatGrid = ({ flight, selectedSeats, setSelectedSeats, tickets }) => {
    //const [selectedSeats, setSelectedSeats] = useState([]);
    if (!flight) {
        return null;
    }

    //console.log("TTTT", tickets);

    const isSeatTaken = (s) => {
        for (var i = 0; i < tickets.length; i++) {
            //console.log("T", tickets[i]);
            if (tickets[i].flight.flightID === flight.flightID) {
                //console.log("ON F");
                if (tickets[i].seat.seatID === s.seatID) {
                    //console.log("YES");
                    return true;
                }
            }
        }
        return false;
    };

    const seats = flight.plane.seatMap;
    const getSeatsPerRow = () => {
        let x = 0;

        seats.map((s) => {
            if (s.rowNum === 1) {
                x++;
            } else {
                return x;
            }
        });

        return x;
    };

    const seatsPerRow = getSeatsPerRow();

    const rows = [];

    const toggleSeatSelection = (seat) => {
        if (isSeatTaken(seat)) {
            return;
        }
        const isSeatSelected = selectedSeats.find(
            (selectedSeat) => selectedSeat.seatCode === seat.seatCode
        );

        if (isSeatSelected) {
            setSelectedSeats(
                selectedSeats.filter(
                    (selectedSeat) => selectedSeat.seatCode !== seat.seatCode
                )
            );
        } else {
            setSelectedSeats([seat]);
        }
    };
    //console.log(seats);

    const groupedSeats = seats.reduce((acc, seat) => {
        if (!acc[seat.rowNum]) {
            acc[seat.rowNum] = [];
        }
        acc[seat.rowNum].push(seat);
        return acc;
    }, {});

    Object.keys(groupedSeats).forEach((rowNum) => {
        const rowSeats = groupedSeats[rowNum];
        const middleIndex = Math.floor(rowSeats.length / 2);
        rows.push(
            <div key={rowNum} className="seat-row">
                {rowSeats.map((seat, index) => (
                    <button
                        className={`seat-btn ${
                            index === middleIndex || index === middleIndex - 1
                                ? "middle-seat"
                                : ""
                        } ${
                            selectedSeats.find(
                                (selectedSeat) =>
                                    selectedSeat.seatCode === seat.seatCode
                            )
                                ? "selected"
                                : ""
                        } ${isSeatTaken(seat) ? "seat-taken" : ""}`}
                        key={seat.seatCode}
                        onClick={() => toggleSeatSelection(seat)}
                    >
                        {`${seat.rowNum}${seat.seatPos}`}
                        <p className="small">{seat.type}</p>
                    </button>
                ))}
            </div>
        );
    });

    return (
        <div>
            <h2 id="name" style={{ marginBottom: "20px" }}>
                Flight {flight.flightID}
            </h2>
            <div>{rows}</div>
        </div>
    );
};

export default SeatGrid;
