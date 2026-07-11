import axiosInstance from "./axiosInstance.js";

/**
 * Start a new interview session.
 *
 * Backend:
 * POST /api/interviews/start
 *
 * Example request:
 * {
 *   role: "Java Backend Developer",
 *   difficulty: "MEDIUM",
 *   numberOfQuestions: 5
 * }
 */
export async function startInterview(interviewData) {
  try {
    const response = await axiosInstance.post(
      "/interviews/start",
      interviewData,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to start the interview.",
    );
  }
}

/**
 * Submit an answer for a question.
 *
 * Backend:
 * POST /api/interviews/{sessionId}/answer
 *
 * Example request:
 * {
 *   questionId: "question-uuid",
 *   answer: "Candidate answer"
 * }
 */
export async function submitAnswer(sessionId, answerData) {
  validateSessionId(sessionId);

  try {
    const response = await axiosInstance.post(
      `/interviews/${sessionId}/answer`,
      answerData,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to submit the answer.",
    );
  }
}

/**
 * Alternative function name.
 *
 * You can call either:
 * submitAnswer(...)
 *
 * or:
 * submitInterviewAnswer(...)
 */
export async function submitInterviewAnswer(
  sessionId,
  answerData,
) {
  return submitAnswer(sessionId, answerData);
}

/**
 * Get the final result of an interview.
 *
 * Backend:
 * GET /api/interviews/{sessionId}/result
 */
export async function getInterviewResult(sessionId) {
  validateSessionId(sessionId);

  try {
    const response = await axiosInstance.get(
      `/interviews/${sessionId}/result`,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to load the interview result.",
    );
  }
}

/**
 * Alternative shorter method name.
 */
export async function getResult(sessionId) {
  return getInterviewResult(sessionId);
}

/**
 * Get a question by its ID.
 *
 * This requires a matching backend endpoint:
 * GET /api/interviews/{sessionId}/questions/{questionId}
 *
 * Do not use this until this endpoint exists in Spring Boot.
 */
export async function getQuestion(sessionId, questionId) {
  validateSessionId(sessionId);

  if (!questionId) {
    throw new Error("Question ID is required.");
  }

  try {
    const response = await axiosInstance.get(
      `/interviews/${sessionId}/questions/${questionId}`,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to load the interview question.",
    );
  }
}

/**
 * Get the current unanswered question.
 *
 * This requires:
 * GET /api/interviews/{sessionId}/question/current
 */
export async function getCurrentQuestion(sessionId) {
  validateSessionId(sessionId);

  try {
    const response = await axiosInstance.get(
      `/interviews/${sessionId}/question/current`,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to load the current question.",
    );
  }
}

/**
 * Get all questions belonging to an interview.
 *
 * This requires:
 * GET /api/interviews/{sessionId}/questions
 */
export async function getInterviewQuestions(sessionId) {
  validateSessionId(sessionId);

  try {
    const response = await axiosInstance.get(
      `/interviews/${sessionId}/questions`,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to load interview questions.",
    );
  }
}

/**
 * Get one submitted answer.
 *
 * This requires:
 * GET /api/interviews/{sessionId}/answers/{questionId}
 */
export async function getAnswer(sessionId, questionId) {
  validateSessionId(sessionId);

  if (!questionId) {
    throw new Error("Question ID is required.");
  }

  try {
    const response = await axiosInstance.get(
      `/interviews/${sessionId}/answers/${questionId}`,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to load the submitted answer.",
    );
  }
}

/**
 * Get all submitted answers for an interview.
 *
 * This requires:
 * GET /api/interviews/{sessionId}/answers
 */
export async function getAllAnswers(sessionId) {
  validateSessionId(sessionId);

  try {
    const response = await axiosInstance.get(
      `/interviews/${sessionId}/answers`,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to load interview answers.",
    );
  }
}

/**
 * Get interview session details.
 *
 * This requires:
 * GET /api/interviews/{sessionId}
 */
export async function getInterviewSession(sessionId) {
  validateSessionId(sessionId);

  try {
    const response = await axiosInstance.get(
      `/interviews/${sessionId}`,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to load the interview session.",
    );
  }
}

/**
 * Get interview history for the logged-in user.
 *
 * This requires:
 * GET /api/interviews/history
 */
export async function getInterviewHistory() {
  try {
    const response = await axiosInstance.get(
      "/interviews/history",
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to load interview history.",
    );
  }
}

/**
 * Delete or cancel an interview session.
 *
 * This requires:
 * DELETE /api/interviews/{sessionId}
 */
export async function deleteInterview(sessionId) {
  validateSessionId(sessionId);

  try {
    const response = await axiosInstance.delete(
      `/interviews/${sessionId}`,
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to delete the interview.",
    );
  }
}

/**
 * Validate session ID before calling the backend.
 */
function validateSessionId(sessionId) {
  if (!sessionId || String(sessionId).trim() === "") {
    throw new Error("Interview session ID is required.");
  }
}

/**
 * Convert Axios errors into readable errors.
 */
function handleApiError(error, defaultMessage) {
  const responseData = error.response?.data;

  let message = defaultMessage;

  if (typeof responseData === "string") {
    message = responseData;
  } else {
    message =
      responseData?.message ||
      responseData?.error ||
      responseData?.details ||
      error.message ||
      defaultMessage;
  }

  const apiError = new Error(message);

  apiError.status = error.response?.status;
  apiError.data = responseData;

  return apiError;
}