import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const Signin = ({ user, setUser }) => {
    const [formData, setFormData] = useState({
        name: "",
        password: "",
        email: "",
        addr: "",
    });
    const navigate = useNavigate();
    const [isRegistering, setIsRegistering] = useState(false);
    //const [user, setUser] = useState(null);

    const handleInputChange = (event) => {
        setFormData({ ...formData, [event.target.name]: event.target.value });
    };

    const [err, setErr] = useState(false);

    const handleSubmit = (event) => {
        event.preventDefault();
        // Handle form submission here
        if (isRegistering) {
            fetch(
                `http://localhost:8080/api/register?email=${encodeURIComponent(
                    formData.email
                )}&password=${encodeURIComponent(
                    formData.password
                )}&name=${encodeURIComponent(
                    formData.name
                )}&addr=${encodeURIComponent(formData.addr)}`
            )
                .then((response) => response.json())
                .then((data) => setUser(data[0]))
                .catch((error) => {
                    console.error("Error:", error);
                });
            window.alert(
                "You Have Been Approved For a Company Card!\nYour Details are as follows:\nCard Number: 2134 1231 1231 1231\nCCV: 354\nExp: 02/2027"
            );
        } else {
            //LOGIN
            fetch(
                "http://localhost:8080/api/login?email=" +
                    encodeURIComponent(formData.email) +
                    "&password=" +
                    encodeURIComponent(formData.password)
            )
                .then((response) => response.json())
                .then((data) => setUser(data[0]))
                .catch((error) => {
                    console.error("Error:", error);
                });
        }
        if (user === null) {
            setErr(true);
        } else {
            setErr(false);
        }
    };

    useEffect(() => {
        if (user !== null) {
            navigate("/demo/build");
        }
    }, [user]);

    //console.log(user);

    const toggleRegistering = () => {
        setIsRegistering(!isRegistering);
    };

    return (
        <div className="container">
            <label className="switch">
                <input type="checkbox" onClick={toggleRegistering} />
                <span className="slider round"></span>
            </label>
            <div>{isRegistering ? "Register" : "Sign In"}</div>
            <form className="form" onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    placeholder="Email"
                    required
                />
                <input
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleInputChange}
                    placeholder="Password"
                    required
                />
                {isRegistering && (
                    <>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleInputChange}
                            placeholder="Name"
                            required
                        />
                        <input
                            type="text"
                            name="addr"
                            value={formData.addr}
                            onChange={handleInputChange}
                            placeholder="Address"
                            required
                        />
                        <input
                            type="checkbox"
                            name="applyCard"
                            value={formData.applyCard}
                            onChange={handleInputChange}
                        />
                        <label htmlFor="applyCard">
                            Apply for a company card
                        </label>
                    </>
                )}
                {err ? (
                    <h6 style={{ color: "red" }}>
                        Error signing/logging in: retry with different
                        credentials
                    </h6>
                ) : null}
                <button id="s-btn" type="submit">
                    {isRegistering ? "Register" : "Sign In"}
                </button>
            </form>
        </div>
    );
};

export default Signin;
