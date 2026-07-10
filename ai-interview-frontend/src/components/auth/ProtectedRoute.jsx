import { Navigate, Outlet } from "react-router";
import { useAuth } from "../../context/AuthContext";
import Loader from "../common/Loader";

function ProtectedRoute() {
  const { isAuthenticated, initializing } = useAuth();

  if (initializing) {
    return <Loader message="Loading your account..." />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

export default ProtectedRoute;