import { Link } from "react-router";
import { useAuth } from "../context/AuthContext";

function DashboardPage() {
  const { user } = useAuth();

  return (
    <main className="page-container">
      <section className="hero-card">
        <div>
          <span className="eyebrow">AI Interview Platform</span>

          <h1>
            Welcome, {user?.fullName || user?.email || "Candidate"}
          </h1>

          <p>
            Practice technical interviews and receive Gemini-powered
            feedback on every answer.
          </p>

          <Link
            to="/interview/start"
            className="button button-primary"
          >
            Start New Interview
          </Link>
        </div>
      </section>

      <section className="feature-grid">
        <article className="feature-card">
          <h2>Role-based interviews</h2>
          <p>
            Generate questions based on your target role and
            experience level.
          </p>
        </article>

        <article className="feature-card">
          <h2>AI evaluation</h2>
          <p>
            Receive scores, strengths, missing points and improved
            answers.
          </p>
        </article>

        <article className="feature-card">
          <h2>Performance results</h2>
          <p>
            Review your final score and question-wise interview
            performance.
          </p>
        </article>
      </section>
    </main>
  );
}

export default DashboardPage;