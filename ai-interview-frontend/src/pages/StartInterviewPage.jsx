import { useState } from "react";
import { useNavigate } from "react-router";
import { startInterview } from "../api/interviewApi";
export default function StartInterviewPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({topic:"Java and Spring Boot",targetRole:"Java Backend Developer",experienceLevel:"INTERMEDIATE",totalQuestions:5});
  const [busy,setBusy] = useState(false);
  const [error,setError] = useState("");
  async function submit(e) {
    e.preventDefault(); setBusy(true); setError("");
    try { const session = await startInterview(form); navigate(`/interview/${session.sessionId}`); }
    catch(err) { setError(err.message); } finally { setBusy(false); }
  }
  return <main className="page-container narrow-container"><section className="form-card">
    <span className="eyebrow">Interview configuration</span><h1>Start a new interview</h1>
    {error && <p role="alert" className="form-error">{error}</p>}
    <form className="form" onSubmit={submit}>
      <label>Topic<input required maxLength="200" value={form.topic} onChange={e => setForm({...form,topic:e.target.value})} /></label>
      <label>Target role<input required maxLength="200" value={form.targetRole} onChange={e => setForm({...form,targetRole:e.target.value})} /></label>
      <label>Experience level<select value={form.experienceLevel} onChange={e => setForm({...form,experienceLevel:e.target.value})}>
        {["BEGINNER","INTERMEDIATE","ADVANCED"].map(level => <option key={level}>{level}</option>)}</select></label>
      <label>Number of questions<input type="number" min="1" max="20" required value={form.totalQuestions} onChange={e => setForm({...form,totalQuestions:Number(e.target.value)})} /></label>
      <button className="button button-primary" disabled={busy}>{busy ? "Generating questions..." : "Start interview"}</button>
    </form></section></main>;
}
