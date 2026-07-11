import { useState } from "react";
import { useNavigate } from "react-router";
import { startInterview } from "../api/interviewApi";
import ErrorMessage from "../components/common/ErrorMessage";

function StartInterviewPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    role: "Java Backend Developer",
    difficulty: "MEDIUM",
    numberOfQuestions: 5,
  });

  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]:
        name === "numberOfQuestions"
          ? Number(value)
          : value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSubmitting(true);

    try {
      const response = await startInterview(formData);

      const sessionId =
        response.sessionId ??
        response.interviewSessionId ??
        response.id;

      if (!sessionId) {
        throw new Error(
          "The backend response does not contain a session ID.",
        );
      }

      navigate(`/interview/${sessionId}`, {
        state: {
          interview: response,
        },
      });
    } catch (apiError) {
      setError(
        apiError.response?.data?.message ||
          apiError.response?.data?.error ||
          apiError.message ||
          "Unable to start the interview.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="page-container narrow-container">
      <section className="form-card">
        <span className="eyebrow">Interview configuration</span>
        <h1>Start a new interview</h1>

        <ErrorMessage message={error} />

        <form onSubmit={handleSubmit} className="form">
          <label>
            Target role
            <input
              type="text"
              name="role"
              value={formData.role}
              onChange={handleChange}
              required
            />
          </label>

          <label>
            Difficulty
            <select
              name="difficulty"
              value={formData.difficulty}
              onChange={handleChange}
            >
              <option value="EASY">Easy</option>
              <option value="MEDIUM">Medium</option>
              <option value="HARD">Hard</option>
            </select>
          </label>

          <label>
            Number of questions
            <input
              type="number"
              name="numberOfQuestions"
              value={formData.numberOfQuestions}
              onChange={handleChange}
              min="1"
              max="20"
              required
            />
          </label>

          <button
            type="submit"
            className="button button-primary"
            disabled={submitting}
          >
            {submitting
              ? "Generating questions..."
              : "Start Interview"}
          </button>
        </form>
      </section>
    </main>
  );
}

export default StartInterviewPage;