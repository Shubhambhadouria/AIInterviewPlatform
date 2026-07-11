import axiosInstance from "./axiosInstance.js";

/**
 * Register a new user.
 *
 * Backend:
 * POST /api/auth/register
 */
export async function registerUser(registerData) {
  try {
    const response = await axiosInstance.post(
      "/auth/register",
      registerData,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(error, "Unable to register user.");
  }
}

/**
 * Log in an existing user.
 *
 * Backend:
 * POST /api/auth/login
 */
export async function loginUser(loginData) {
  try {
    const response = await axiosInstance.post(
      "/auth/login",
      loginData,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(error, "Unable to log in.");
  }
}

/**
 * Get the currently authenticated user.
 *
 * Backend:
 * GET /api/auth/me
 */
export async function getCurrentUser() {
  try {
    const response = await axiosInstance.get("/auth/me");

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to load the current user.",
    );
  }
}

/**
 * Frontend logout.
 *
 * JWT authentication is stateless, so usually no backend
 * logout endpoint is required. The token is removed locally.
 */
export function logoutUser() {
  localStorage.removeItem("ai_interview_access_token");
}

/**
 * Convert Axios errors into readable JavaScript errors.
 */
function handleApiError(error, defaultMessage) {
  const message =
    error.response?.data?.message ||
    error.response?.data?.error ||
    error.response?.data?.details ||
    error.message ||
    defaultMessage;

  const apiError = new Error(message);

  apiError.status = error.response?.status;
  apiError.data = error.response?.data;

  return apiError;
}