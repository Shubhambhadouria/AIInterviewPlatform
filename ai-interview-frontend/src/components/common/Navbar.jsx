import { Link, useNavigate } from "react-router";
import { useAuth } from "../../context/AuthContext";

function Navbar() {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <header className="navbar">
      <Link to="/" className="brand">
        AI Interview Coach
      </Link>

      <nav className="nav-links">
        {isAuthenticated ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/interview/start">Start Interview</Link>
            <Link to="/resume-preparation">Resume Preparation</Link>

            <Link to="/question-bank/new">Question Bank</Link>

            <span className="user-email">{user?.fullName || user?.email}</span>

            <button
              type="button"
              className="button button-secondary"
              onClick={handleLogout}
            >
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </nav>
    </header>
  );
}

export default Navbar;
