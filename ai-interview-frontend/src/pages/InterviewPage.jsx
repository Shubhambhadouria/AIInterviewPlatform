import { useEffect, useState } from "react";
import { Link, useParams } from "react-router";
import { getInterviewSession, submitInterviewAnswer } from "../api/interviewApi";
export default function InterviewPage() {
  const { sessionId } = useParams();
  const [session,setSession] = useState(null);
  const [answer,setAnswer] = useState("");
  const [feedback,setFeedback] = useState(null);
  const [busy,setBusy] = useState(false);
  const [error,setError] = useState("");
  useEffect(() => {
    let active = true;
    getInterviewSession(sessionId).then(data => {if(active) setSession(data);})
      .catch(err => {if(active) setError(err.message);});
    return () => {active=false;};
  },[sessionId]);
  async function submit(e) {
    e.preventDefault(); setBusy(true);setError("");
    try {
      const response = await submitInterviewAnswer(sessionId,{questionId:session.questions[0].questionId,answer:answer.trim()});
      setFeedback(response);
      setSession({...session,status:response.completed ? "COMPLETED" : "IN_PROGRESS",answeredQuestions:session.answeredQuestions+1,questions:session.questions.slice(1)});
      setAnswer("");
    } catch(err) {setError(err.message);} finally {setBusy(false);}
  }
  return <main className="page-container interview-container">
    <section className="interview-header"><div><span className="eyebrow">Interview practice</span><h1>{session?.title || "Loading interview..."}</h1></div>
      {session && <span className="question-counter">{session.answeredQuestions} of {session.totalQuestions} answered</span>}</section>
    {error && <p role="alert" className="form-error">{error}</p>}
    {feedback && <section className="feedback-card" aria-live="polite"><h2>Your feedback · {feedback.score}/10</h2><p>{feedback.feedback}</p>
      <h3>Improved answer</h3><p className="preserve-lines">{feedback.improvedAnswer}</p></section>}
    {session?.status === "COMPLETED" ? <section className="question-card"><h2>Interview complete</h2><Link className="button button-primary" to={`/interview/${sessionId}/result`}>View results</Link></section> :
      session?.questions?.[0] && <section className="question-card"><span className="question-label">Question {session.answeredQuestions+1} of {session.totalQuestions}</span>
      <h2>{session.questions[0].questionText}</h2><form className="form" onSubmit={submit}><label>Your answer<textarea rows="10" required maxLength="5000" value={answer} onChange={e => setAnswer(e.target.value)} disabled={busy} /></label>
      <button className="button button-primary" disabled={busy || !answer.trim()}>{busy ? "Evaluating..." : "Submit answer"}</button></form></section>}
  </main>;
}
