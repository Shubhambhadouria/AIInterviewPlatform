import { useEffect, useState } from "react";
import { Link, useParams } from "react-router";
import { getInterviewResult } from "../api/interviewApi.js";
import ErrorMessage from "../components/common/ErrorMessage";
import Loader from "../components/common/Loader";

function ResultPage() {
  const { sessionId } = useParams();

  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    async function loadResult() {
      try {
        const response = await getInterviewResult(sessionId);

        if (active) {
          setResult(response);
        }
      } catch (apiError) {
        if (active) {
          setError(
            apiError.response?.data?.message ||
              apiError.response?.data?.error ||
              "Unable to load the interview result.",
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadResult();

    return () => {
      active = false;
    };
  }, [sessionId]);

  if (loading) {
    return <Loader message="Preparing your final result..." />;
  }

  if (error) {
    return (
      <main className="page-container">
        <ErrorMessage message={error} />

        <Link
          to="/dashboard"
          className="button button-secondary"
        >
          Return to Dashboard
        </Link>
      </main>
    );
  }

  const questionResults =
    result?.questionResults ??
    result?.questions ??
    result?.results ??
    [];

  const overallScore =
    result?.overallScore ??
    result?.score ??
    result?.totalScore ??
    0;

  return (
    <main className="page-container result-container">
      <section className="result-summary">
        <div>
          <span className="eyebrow">Interview completed</span>
          <h1>Your interview result</h1>

          <p>
            Review your overall performance and question-wise
            feedback.
          </p>
        </div>

        <div className="overall-score">
          <strong>{overallScore}</strong>
          <span>Overall score</span>
        </div>
      </section>

      {result?.overallFeedback && (
        <section className="feedback-card">
          <h2>Overall feedback</h2>
          <p>{result.overallFeedback}</p>
        </section>
      )}

      <section className="result-list">
        {questionResults.map((item, index) => (
          <article
            className="result-question-card"
            key={item.questionId ?? item.id ?? index}
          >
            <div className="feedback-heading">
              <h2>Question {index + 1}</h2>

              <span className="score-badge">
                {item.score ?? 0}/10
              </span>
            </div>

            <h3>
              {item.questionText ??
                item.question ??
                "Interview question"}
            </h3>

            <div className="result-section">
              <h4>Your answer</h4>
              <p>{item.userAnswer ?? item.answer}</p>
            </div>

            <div className="result-section">
              <h4>AI feedback</h4>
              <p>{item.aiFeedback ?? item.feedback}</p>
            </div>

            {item.improvedAnswer && (
              <div className="result-section">
                <h4>Improved answer</h4>
                <p>{item.improvedAnswer}</p>
              </div>
            )}
          </article>
        ))}
      </section>

      <div className="result-actions">
        <Link
          to="/interview/start"
          className="button button-primary"
        >
          Start Another Interview
        </Link>

        <Link
          to="/dashboard"
          className="button button-secondary"
        >
          Dashboard
        </Link>
      </div>
    </main>
  );
}

export default ResultPage;