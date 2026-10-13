import { useEffect, useState } from "react";
import { Link } from "react-router";
import { useAuth } from "../context/authState";
import { getInterviewHistory } from "../api/interviewApi";
export default function DashboardPage() {
  const {user} = useAuth();
  const [history,setHistory] = useState(null);
  const [error,setError] = useState("");
  useEffect(() => {let active=true;getInterviewHistory().then(data => {if(active) setHistory(data);}).catch(err => {if(active) setError(err.message);});return () => {active=false;};},[]);
  const completed = history?.filter(s => s.status === "COMPLETED") || [];
  const average = completed.length ? (completed.reduce((total,s) => total + s.overallScore,0)/completed.length).toFixed(1) : "—";
  return <main className="page-container"><section className="hero-card"><div><span className="eyebrow">AI Interview Coach</span><h1>Welcome, {user?.fullName || user?.email || "Candidate"}</h1><p>Practice interviews and prepare answers grounded in your own experience.</p>
    <div className="action-row"><Link className="button button-primary" to="/interview/start">Start interview</Link><Link className="button button-secondary" to="/resume-preparation">Prepare from resume</Link></div></div></section>
    <section className="feature-grid"><article className="feature-card"><h2>{completed.length}</h2><p>Completed interviews</p></article><article className="feature-card"><h2>{average}</h2><p>Average score out of 10</p></article><article className="feature-card"><h2>Preparation library</h2><Link to="/question-banks">Review saved question banks</Link></article></section>
    <h2>Interview history</h2>{error && <p role="alert" className="form-error">{error}</p>}{!history && !error && <p>Loading history...</p>}{history?.length === 0 && <p>Your interviews will appear here.</p>}
    <section className="result-list">{history?.map(session => <article className="feature-card" key={session.sessionId}><h3>{session.title}</h3><p>{new Date(session.createdAt).toLocaleDateString()} · {session.answeredQuestions}/{session.totalQuestions} answered · {session.status}</p>
      <Link to={session.status === "COMPLETED" ? `/interview/${session.sessionId}/result` : `/interview/${session.sessionId}`}>{session.status === "COMPLETED" ? `View results · ${session.overallScore}/10` : "Resume interview"}</Link></article>)}</section>
  </main>;
}
