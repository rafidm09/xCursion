import React, { useState, useEffect } from "react";

const ManageFlightsPage = ({ flight, getAllFlightInfo }) => {
    const [origin, setOrigin] = useState(flight.origin);
    const [destination, setDestination] = useState(flight.destination);
    const [departTime, setDepartTime] = useState(flight.departTime);
    const [arrivalTime, setArrivalTime] = useState(flight.arrivalTime);
    const [costReg, setCostReg] = useState(flight.costReg);
    const [costBusiness, setCostBusiness] = useState(flight.costFirstClass);
    const [plane, setPlane] = useState(flight.plane.planeID);
    const [crew, setCrew] = useState(flight.crewList[0].flightCrewID);

    const [crews, setCrews] = useState([]);
    const [planes, setPlanes] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch(
                    "http://localhost:3000/api/getAllCrews"
                );
                const data = await response.json();

                const grouped = data.reduce((acc, curr) => {
                    (acc[curr.flightCrewID] =
                        acc[curr.flightCrewID] || []).push({
                        name: curr.name,
                        flightAttendantID: curr.flightAttendantID,
                    });
                    return acc;
                }, {});

                const result = Object.keys(grouped).map((key) => ({
                    flightCrewID: key,
                    attendants: grouped[key],
                }));

                setCrews(result);
            } catch (error) {
                console.error("Error:", error);
            }
        };

        const fetchPlanes = async () => {
            await fetch("http://localhost:3000/api/getAllPlanes")
                .then((response) => response.json())
                .then((data) => setPlanes(data))
                .catch((error) => console.error("Error:", error));
        };

        fetchData();
        fetchPlanes();
    }, []);

    //console.log("CREWS", crews);
    //console.log("Planes", planes);

    const handleSubmit = async (event) => {
        event.preventDefault();
        //console.log(crew);
        const url = new URL("http://localhost:8080/api/updateAllFlightInfo");
        const params = {
            fid: flight.flightID,
            origin: origin,
            dest: destination,
            depart: departTime,
            arrival: arrivalTime,
            newcostreg: costReg,
            newcostbusiness: costBusiness,
            cid: crew,
            pid: plane,
        };
        Object.keys(params).forEach((key) =>
            url.searchParams.append(key, params[key])
        );

        await fetch(url)
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }
            })
            .catch((error) => console.error("Error:", error));

        getAllFlightInfo();
    };

    return (
        <form className="manage-flights-form" onSubmit={handleSubmit}>
            <label>
                Origin:
                <input
                    type="text"
                    value={origin}
                    onChange={(e) => setOrigin(e.target.value)}
                />
            </label>
            <label>
                Destination:
                <input
                    type="text"
                    value={destination}
                    onChange={(e) => setDestination(e.target.value)}
                />
            </label>
            <label>
                Departure Time:
                <input
                    type="datetime-local"
                    value={departTime}
                    onChange={(e) => setDepartTime(e.target.value)}
                />
            </label>
            <label>
                Arrival Time:
                <input
                    type="datetime-local"
                    value={arrivalTime}
                    onChange={(e) => setArrivalTime(e.target.value)}
                />
            </label>
            <label>
                Regular Cost:
                <input
                    type="text"
                    value={costReg}
                    onChange={(e) => setCostReg(e.target.value)}
                />
            </label>
            <label>
                Business Cost:
                <input
                    type="text"
                    value={costBusiness}
                    onChange={(e) => setCostBusiness(e.target.value)}
                />
            </label>
            <label>
                Plane:
                <select
                    value={plane}
                    onChange={(e) => setPlane(e.target.value)}
                >
                    {planes.map((plane, index) => (
                        <option key={index} value={plane.planeID}>
                            {plane.model}
                        </option>
                    ))}
                </select>
            </label>
            <label>
                Crew:
                <select value={crew} onChange={(e) => setCrew(e.target.value)}>
                    {crews.map((crewItem, index) => (
                        <option key={index} value={crewItem.flightCrewID}>
                            {`Crew ${
                                crewItem.flightCrewID
                            }: ${crewItem.attendants
                                .map((attendant) => attendant.name)
                                .join(", ")}`}
                        </option>
                    ))}
                </select>
            </label>
            <input type="submit" value="Submit" />
        </form>
    );
};

export default ManageFlightsPage;
