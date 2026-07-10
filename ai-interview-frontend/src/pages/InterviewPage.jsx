import { useMemo, useState } from "react";
import {
  Navigate,
  useLocation,
  useNavigate,
  useParams,
} from "react-router";
import { submitInterviewAnswer } from "../api/interviewApi";
import ErrorMessage from "../components/common/ErrorMessage";

function normalizeQuestion(source) {
  if (!source) {
    return null;
  }

  const question =
    source.currentQuestion ??
    source.question ??
    source.nextQuestion ??
    source.questions?.[0] ??
    source;

  if (!question) {
    return null;
  }

  const questionId =
    question.questionId ??
    question.id ??
    question.interviewQuestionId ??
    source.questionId ??
    null;

  const questionText =
    question.questionText ??
    question.text ??
    question.question ??
    source.questionText ??
    null;

  return {
    id: questionId,
    text: questionText,

    questionNumber:
      source.currentQuestionNumber ??
      source.questionNumber ??
      question.questionNumber ??
      1,

    totalQuestions:
      source.totalQuestions ??
      question.totalQuestions ??
      source.questions?.length ??
      null,
  };
}

function InterviewPage() {
  const { sessionId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();

  const initialInterview = location.state?.interview;
  const initialQuestion = useMemo(
    () => normalizeQuestion(initialInterview),
    [initialInterview],
  );

  const [question, setQuestion] = useState(initialQuestion);
  const [answer, setAnswer] = useState("");
  const [feedback, setFeedback] = useState(null);
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  if (!initialInterview && !question) {
    return <Navigate to="/interview/start" replace />;
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!answer.trim()) {
      setError("Please enter your answer.");
      return;
    }

    if (!question?.id) {
      setError("Question ID is missing in the backend response.");
      return;
    }

    setError("");
    setSubmitting(true);

    try {
      const response = await submitInterviewAnswer(sessionId, {
        questionId: question.id,
        answer: answer.trim(),
      });

      setFeedback({
        score: response.score ?? response.aiScore,
        aiFeedback:
          response.aiFeedback ??
          response.feedback ??
          response.message,
        strengths: response.strengths,
        improvements:
          response.improvements ??
          response.areasForImprovement ??
          response.missingPoints,
      });

      const completed =
        response.completed === true ||
        response.interviewCompleted === true ||
        response.status === "COMPLETED";

      if (completed) {
        setTimeout(() => {
          navigate(`/interview/${sessionId}/result`);
        }, 1200);

        return;
      }

      const nextQuestion = normalizeQuestion(
        response.nextQuestion ?? response,
      );

      if (nextQuestion?.id && nextQuestion?.text) {
        setQuestion(nextQuestion);
        setAnswer("");
      }
    } catch (apiError) {
      setError(
        apiError.response?.data?.message ||
          apiError.response?.data?.error ||
          "Unable to submit the answer.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="page-container interview-container">
      <section className="interview-header">
        <div>
          <span className="eyebrow">Live interview</span>
          <h1>Technical Interview</h1>
        </div>

        {question?.totalQuestions && (
          <span className="question-counter">
            Question {question.questionNumber} of{" "}
            {question.totalQuestions}
          </span>
        )}
      </section>

      <section className="question-card">
        <span className="question-label">
          Question {question?.questionNumber}
        </span>

        <h2>{question?.text}</h2>

        <form onSubmit={handleSubmit} className="form">
          <label>
            Your answer
            <textarea
              value={answer}
              onChange={(event) => setAnswer(event.target.value)}
              rows="10"
              placeholder="Explain your answer clearly, including examples and internal working where relevant."
              disabled={submitting}
              required
            />
          </label>

          <div className="answer-footer">
            <span>{answer.trim().length} characters</span>

            <button
              type="submit"
              className="button button-primary"
              disabled={submitting}
            >
              {submitting
                ? "Gemini is evaluating..."
                : "Submit Answer"}
            </button>
          </div>
        </form>

        <ErrorMessage message={error} />
      </section>

      {feedback && (
        <section className="feedback-card">
          <div className="feedback-heading">
            <h2>Gemini feedback</h2>

            {feedback.score !== undefined && (
              <span className="score-badge">
                {feedback.score}/10
              </span>
            )}
          </div>

          <p>{feedback.aiFeedback}</p>

          {feedback.strengths && (
            <div>
              <h3>Strengths</h3>
              <p>{String(feedback.strengths)}</p>
            </div>
          )}

          {feedback.improvements && (
            <div>
              <h3>Areas to improve</h3>
              <p>{String(feedback.improvements)}</p>
            </div>
          )}
        </section>
      )}
    </main>
  );
}

export default InterviewPage;