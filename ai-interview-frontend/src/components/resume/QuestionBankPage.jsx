import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router";
import api from "../../api/axiosInstance";
import "../../styles/resume-preparation.css";
export default function QuestionBankPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const [profile,setProfile] = useState(null);
  const [count,setCount] = useState(5);
  const [projects,setProjects] = useState(true);
  const [behavioural,setBehavioural] = useState(true);
  const [busy,setBusy] = useState(false);
  const [error,setError] = useState("");
  const profileId = params.get("candidateProfileId");
  useEffect(() => {
    let active=true;
    api.get(profileId ? `/candidate-profiles/${profileId}` : "/candidate-profiles/latest")
      .then(({data}) => {if(active) setProfile(data);})
      .catch(err => {if(active) setError(err.response?.status === 404 ? "Upload and review your resume first." : err.response?.data?.message || err.message);});
    return () => {active=false;};
  },[profileId]);
  async function generate(e) {
    e.preventDefault();setBusy(true);setError("");
    try {
      const {data} = await api.post("/question-banks/generate",{candidateProfileId:profile.id,questionsPerSection:count,includeProjectQuestions:projects,includeBehaviouralQuestions:behavioural});
      navigate(`/question-banks/${data.questionBankId}`);
    } catch(err) {setError(err.response?.data?.message || err.message);} finally {setBusy(false);}
  }
  return <main className="resume-page"><section className="resume-hero"><p className="eyebrow">Personalized preparation</p><h1>Generate your question bank</h1><p>Build technical, project and behavioural questions from your confirmed profile.</p></section>
    {error && <p className="alert alert--error" role="alert">{error}</p>}
    {profile ? <form className="resume-card form-stack" onSubmit={generate}>
      <h2>{profile.fullName} · {profile.targetRole || profile.professionalTitle}</h2>
      {profile.status !== "CONFIRMED" ? <><p>Review and confirm your profile before generating questions.</p><button type="button" className="button button-primary" onClick={() => navigate(`/candidate-profiles/${profile.id}/review`)}>Review profile</button></> : <>
        <label>Questions per section<input type="number" min="1" max="20" required value={count} onChange={e => setCount(Number(e.target.value))} /></label>
        <label><input type="checkbox" checked={projects} onChange={e => setProjects(e.target.checked)} /> Include project questions</label>
        <label><input type="checkbox" checked={behavioural} onChange={e => setBehavioural(e.target.checked)} /> Include behavioural questions</label>
        <button disabled={busy} className="button button-primary">{busy ? "Generating your preparation bank..." : "Generate questions"}</button>
      </>}
    </form> : <button className="button button-primary" onClick={() => navigate("/resume-preparation")}>Upload resume</button>}
  </main>;
}
