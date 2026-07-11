import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import {
  getCurrentUser,
  loginUser,
  registerUser,
} from "../api/authApi";
import {
  getToken,
  removeToken,
  saveToken,
} from "../util/tokenStorage.js";

const AuthContext = createContext(null);

function extractToken(response) {
  return (
    response?.token ??
    response?.accessToken ??
    response?.jwtToken ??
    null
  );
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [initializing, setInitializing] = useState(true);

  const loadCurrentUser = useCallback(async () => {
    const token = getToken();

    if (!token) {
      setInitializing(false);
      return;
    }

    try {
      const currentUser = await getCurrentUser();
      setUser(currentUser);
    } catch {
      removeToken();
      setUser(null);
    } finally {
      setInitializing(false);
    }
  }, []);

  useEffect(() => {
    loadCurrentUser();
  }, [loadCurrentUser]);

  async function login(credentials) {
    const response = await loginUser(credentials);
    const token = extractToken(response);

    if (!token) {
      throw new Error("Login response does not contain a JWT token.");
    }

    saveToken(token);

    const currentUser = response.user ?? {
      email: credentials.email,
      fullName: response.fullName,
    };

    setUser(currentUser);
    return response;
  }

  async function register(registerData) {
    const response = await registerUser(registerData);
    const token = extractToken(response);

    if (token) {
      saveToken(token);

      setUser(
        response.user ?? {
          email: registerData.email,
          fullName: registerData.fullName,
        },
      );
    }

    return response;
  }

  function logout() {
    removeToken();
    setUser(null);
  }

  const contextValue = useMemo(
    () => ({
      user,
      initializing,
      isAuthenticated: Boolean(user || getToken()),
      login,
      register,
      logout,
    }),
    [user, initializing],
  );

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider.");
  }

  return context;
}