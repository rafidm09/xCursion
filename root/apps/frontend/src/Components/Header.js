import { Link } from "react-router-dom";

const Header = ({ user, setUser }) => {
    const signOut = () => {
        setUser(null);
    };

    return (
        <header>
            <Link to="/demo/build">
                <h1>Air480</h1>
            </Link>
            <Link className="active" to="/demo/build">
                Flights
            </Link>
            <Link to="/demo/build/tickets">My Tickets</Link>
            {user && user.type === "Admin" ? (
                <Link to="/demo/build/manage">Manage</Link>
            ) : null}

            <button>
                {user !== null ? (
                    <a onClick={signOut}>Sign out</a> // You might want to replace this with a function that signs the user out
                ) : (
                    <Link to="/demo/build/signin">Signup/Login</Link>
                )}
            </button>
        </header>
    );
};

export default Header;
