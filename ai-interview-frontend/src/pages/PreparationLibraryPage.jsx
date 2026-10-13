import { useEffect, useState } from "react";
import { Link } from "react-router";
import api from "../api/axiosInstance";
export default function PreparationLibraryPage() {
  const [banks,setBanks] = useState(null);
  const [error,setError] = useState("");
  useEffect(() => {let active=true; api.get("/question-banks").then(({data}) => {if(active) setBanks(data);}).catch(err => {if(active) setError(err.response?.data?.message || err.message);});return () => {active=false;};},[]);
  return <main className="page-container"><section className="hero-card"><div><span className="eyebrow">Preparation library</span><h1>Your question banks</h1><p>Revisit saved answers, mark your progress and personalize your preparation.</p><Link className="button button-primary" to="/question-bank/new">Create question bank</Link></div></section>
    {error && <p role="alert" className="form-error">{error}</p>}
    {!banks && !error && <p>Loading question banks...</p>}
    {banks?.length === 0 && <p>No question banks yet. Upload a resume to get started.</p>}
    <section className="feature-grid">{banks?.map(bank => <article className="feature-card" key={bank.questionBankId}><h2>{bank.title}</h2><p>{bank.status} · {bank.totalQuestions} questions</p>
      {bank.status === "READY" ? <Link to={`/question-banks/${bank.questionBankId}`}>Open question bank</Link> : <p>{bank.message}</p>}</article>)}</section>
  </main>;
}
