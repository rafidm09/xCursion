import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const ManagerPage = () => {
    const navigate = useNavigate();

    const [flightId, setFlightId] = useState("");
    const [departTime, setDepartTime] = useState("");
    const [arrivalTime, setArrivalTime] = useState("");
    const [origin, setOrigin] = useState("");
    const [dest, setDest] = useState("");
    const [costRegular, setCostRegular] = useState("");
    const [costBusiness, setCostBusiness] = useState("");
    const [plane, setPlane] = useState("");
    const [crew, setCrew] = useState("");

    const [planeID, setPlaneID] = useState("");
    const [model, setModel] = useState("");
    const [crewSize, setCrewSize] = useState("");
    const [status, setStatus] = useState("");

    const [crewID, setCrewID] = useState("");
    const [name1, setName1] = useState("");
    const [name2, setName2] = useState("");
    const [name3, setName3] = useState("");
    const [name4, setName4] = useState("");

    const [crews, setCrews] = useState([]);
    const [planes, setPlanes] = useState([]);
    const [flights, setFlights] = useState([]);

    const [selectedPlaneID, setSelectedPlaneID] = useState("");

    useEffect(() => {
        reloadDB();
        //console.log("f", flights);
    }, []);

    const reloadDB = () => {
        fetchData();
        fetchPlanes();
        getAllFlightInfo();
    };

    const confirm = () => {
        navigate("/demo/build/confirm");

        setTimeout(() => {
            navigate("/demo/build/manage");
        }, 1000);
    };

    const fetchData = async () => {
        try {
            const response = await fetch(
                "http://localhost:3000/api/getAllCrews"
            );
            const data = await response.json();

            const grouped = data.reduce((acc, curr) => {
                (acc[curr.flightCrewID] = acc[curr.flightCrewID] || []).push({
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

    const getAllFlightInfo = async () => {
        await fetch("http://localhost:8080/api/getFlightInfo")
            .then((response) => response.json())
            .then((data) => setFlights(data))
            .catch((e) => console.log(e));
    };

    const fetchPlanes = async () => {
        await fetch("http://localhost:3000/api/getAllPlanes")
            .then((response) => response.json())
            .then((data) => setPlanes(data))
            .catch((error) => console.error("Error:", error));
    };

    const [selectedOption, setSelectedOption] = useState("flight");

    const handleSelect = (option) => {
        setSelectedOption(option);
    };

    const [selectedAction, setSelectedAction] = useState("add");

    const handleActionSelect = (action) => {
        setSelectedAction(action);
    };

    const addFlight = (event) => {
        event.preventDefault();

        fetch(
            `http://localhost:8080/api/addFlight?fid=${flightId}&depart=${departTime}&arrival=${arrivalTime}&origin=${origin}&dest=${dest}&costregular=${costRegular}&costbusiness=${costBusiness}&pid=${plane}&cid=${crew}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }
        ).catch((error) => {
            console.error("Error:", error);
        });
        confirm();
    };

    const [selectedFlight, setSelectedFlight] = useState("");

    const handleOptionSelect = (event) => {
        setSelectedFlight(event.target.value);
    };

    const removeFlight = async (event) => {
        event.preventDefault();
        await fetch(
            `http://localhost:8080/api/removeFlight?fid=${selectedFlight}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }
        ).catch((error) => {
            console.error("Error:", error);
        });
        confirm();
    };

    const addPlane = async (event) => {
        event.preventDefault();
        await fetch(
            `http://localhost:8080/api/addPlane?pid=${planeID}&model=${model}&crewsize=${crewSize}&status=${status}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }
        ).catch((error) => {
            console.error("Error:", error);
        });
        confirm();
    };

    const deletePlane = async (event) => {
        event.preventDefault();

        await fetch(
            `http://localhost:8080/api/removePlane?pid=${selectedPlaneID}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }
        ).catch((error) => {
            console.error("Error:", error);
        });
        confirm();
    };

    const addCrew = async (event) => {
        event.preventDefault();

        await fetch(
            `http://localhost:8080/api/addCrew?cid=${crewID}&name1=${name1}&name2=${name2}&name3=${name3}&name4=${name4}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }
        ).catch((error) => {
            console.error("Error:", error);
        });
        confirm();
    };

    const [selectedCrewID, setSelectedCrewID] = useState("");

    const removeCrew = async (event) => {
        event.preventDefault();
        await fetch(
            `http://localhost:8080/api/removeCrew?cid=${selectedCrewID}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }
        ).catch((error) => {
            console.error("Error:", error);
        });
        confirm();
    };

    return (
        <div>
            <div className="options-container">
                <div
                    className={`option ${
                        selectedOption === "flight" ? "selected" : ""
                    }`}
                    onClick={() => handleSelect("flight")}
                >
                    Manage Flights
                </div>
                <div
                    className={`option ${
                        selectedOption === "plane" ? "selected" : ""
                    }`}
                    onClick={() => handleSelect("plane")}
                >
                    Manage Planes
                </div>
                <div
                    className={`option ${
                        selectedOption === "crew" ? "selected" : ""
                    }`}
                    onClick={() => handleSelect("crew")}
                >
                    Manage Crews
                </div>
            </div>
            <div className="actions-container">
                <div
                    className={`action ${
                        selectedAction === "add" ? "selected" : ""
                    }`}
                    onClick={() => handleActionSelect("add")}
                >
                    Add
                </div>
                <div
                    className={`action ${
                        selectedAction === "delete" ? "selected" : ""
                    }`}
                    onClick={() => handleActionSelect("delete")}
                >
                    Delete
                </div>
            </div>
            {selectedOption === "flight" && selectedAction === "add" && (
                <form className="myForm" onSubmit={addFlight}>
                    <label>
                        Flight ID:
                        <input
                            type="text"
                            value={flightId}
                            onChange={(e) => setFlightId(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Departure Time:
                        <input
                            type="datetime-local"
                            value={departTime}
                            onChange={(e) => setDepartTime(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Arrival Time:
                        <input
                            type="datetime-local"
                            value={arrivalTime}
                            onChange={(e) => setArrivalTime(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Origin:
                        <input
                            type="text"
                            value={origin}
                            onChange={(e) => setOrigin(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Destination:
                        <input
                            type="text"
                            value={dest}
                            onChange={(e) => setDest(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Cost (Regular):
                        <input
                            type="text"
                            value={costRegular}
                            onChange={(e) => setCostRegular(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Cost (Business):
                        <input
                            type="text"
                            value={costBusiness}
                            onChange={(e) => setCostBusiness(e.target.value)}
                            required
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
                        <select
                            value={crew}
                            onChange={(e) => setCrew(e.target.value)}
                        >
                            {crews.map((crewItem, index) => (
                                <option
                                    key={index}
                                    value={crewItem.flightCrewID}
                                >
                                    {`Crew ${
                                        crewItem.flightCrewID
                                    }: ${crewItem.attendants
                                        .map((attendant) => attendant.name)
                                        .join(", ")}`}
                                </option>
                            ))}
                        </select>
                    </label>
                    <button style={{ color: "white" }} type="submit">
                        Submit
                    </button>
                </form>
            )}
            {selectedOption === "flight" && selectedAction === "delete" && (
                <form className="myForm" onSubmit={removeFlight}>
                    <label>
                        Select Flight to Delete:
                        <select
                            value={selectedFlight}
                            onChange={handleOptionSelect}
                            required
                        >
                            {flights.map((flight) => (
                                <option value={flight.flightID}>
                                    {flight.flightID}: {flight.origin} &rarr;{" "}
                                    {flight.destination}
                                </option>
                            ))}
                        </select>
                    </label>
                    <button style={{ color: "white" }} type="submit">
                        Submit
                    </button>
                </form>
            )}
            {selectedOption === "plane" && selectedAction === "add" && (
                <form className="myForm" onSubmit={addPlane}>
                    <label>
                        Plane ID:
                        <input
                            type="text"
                            value={planeID}
                            onChange={(e) => setPlaneID(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Model:
                        <input
                            type="text"
                            value={model}
                            onChange={(e) => setModel(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Crew Size:
                        <input
                            type="number"
                            value={crewSize}
                            onChange={(e) => setCrewSize(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Status:
                        <input
                            type="text"
                            value={status}
                            onChange={(e) => setStatus(e.target.value)}
                            required
                        />
                    </label>
                    <button type="submit">Add Plane</button>
                </form>
            )}
            {selectedOption === "plane" && selectedAction === "delete" && (
                <form className="myForm" onSubmit={deletePlane}>
                    <label>
                        Select Plane to Delete:
                        <select
                            value={selectedPlaneID}
                            onChange={(e) => setSelectedPlaneID(e.target.value)}
                            required
                        >
                            {planes.map((plane) => (
                                <option value={plane.planeID}>
                                    {`Plane ${plane.planeID}: ${plane.model}`}
                                </option>
                            ))}
                        </select>
                    </label>
                    <button type="submit">Delete Plane</button>
                </form>
            )}
            {selectedOption === "crew" && selectedAction === "add" && (
                <form className="myForm" onSubmit={addCrew}>
                    <label>
                        Crew ID:
                        <input
                            type="text"
                            value={crewID}
                            onChange={(e) => setCrewID(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Name 1:
                        <input
                            type="text"
                            value={name1}
                            onChange={(e) => setName1(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Name 2:
                        <input
                            type="text"
                            value={name2}
                            onChange={(e) => setName2(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Name 3:
                        <input
                            type="text"
                            value={name3}
                            onChange={(e) => setName3(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Name 4:
                        <input
                            type="text"
                            value={name4}
                            onChange={(e) => setName4(e.target.value)}
                            required
                        />
                    </label>
                    <button type="submit">Add Crew</button>
                </form>
            )}
            {selectedOption === "crew" && selectedAction === "delete" && (
                <form className="myForm" onSubmit={removeCrew}>
                    <label>
                        Select Crew to Remove:
                        <select
                            value={selectedCrewID}
                            onChange={(e) => setSelectedCrewID(e.target.value)}
                            required
                        >
                            {crews.map((crewItem, index) => (
                                <option
                                    key={index}
                                    value={crewItem.flightCrewID}
                                >
                                    {`Crew ${
                                        crewItem.flightCrewID
                                    }: ${crewItem.attendants
                                        .map((attendant) => attendant.name)
                                        .join(", ")}`}
                                </option>
                            ))}
                        </select>
                    </label>
                    <button type="submit">Remove Crew</button>
                </form>
            )}
        </div>
    );
};

export default ManagerPage;
