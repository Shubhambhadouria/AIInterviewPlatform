import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getCandidateProfile, updateCandidateProfile, confirmCandidateProfile } from "../api/candidateProfileService";
import "../styles/CandidateProfileReviewPage.css";

const categories = ["PROGRAMMING_LANGUAGE","BACKEND","FRONTEND","DATABASE","CACHE","MESSAGING","CLOUD","DEVOPS","TESTING","SYSTEM_DESIGN","TOOL","OTHER"];
export default function CandidateProfileReviewPage() {
  const { profileId } = useParams();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [busy, setBusy] = useState(false);
  useEffect(() => {
    let active = true;
    getCandidateProfile(profileId).then(data => { if (active) setProfile(data); })
      .catch(err => { if (active) setError(err.response?.data?.message || err.message); });
    return () => { active = false; };
  }, [profileId]);
  const field = (name, value) => setProfile(p => ({...p, [name]: value}));
  const item = (collection, index, name, value) => setProfile(p => ({
    ...p, [collection]: p[collection].map((entry, i) => i === index ? {...entry, [name]: value} : entry)
  }));
  async function save(confirm) {
    if (!profile.fullName?.trim() || !profile.professionalTitle?.trim() || !profile.targetRole?.trim() ||
        !profile.skills.length || profile.skills.some(s => !s.skillName?.trim())) {
      setError("Enter your name, title, target role and at least one named skill."); return;
    }
    setBusy(true); setError(""); setNotice("");
    try {
      const updated = await updateCandidateProfile(profileId, profile);
      setProfile(confirm ? await confirmCandidateProfile(profileId) : updated);
      setNotice(confirm ? "Profile confirmed. You can generate your question bank." : "Changes saved.");
    } catch (err) { setError(err.response?.data?.message || err.message); }
    finally { setBusy(false); }
  }
  if (!profile) return <main className="profile-page">{error ? <p role="alert">{error}</p> : <p>Loading profile...</p>}</main>;
  return <main className="profile-page">
    <section className="profile-header"><div><p className="profile-eyebrow">Resume preparation</p><h1>Review your candidate profile</h1>
      <p>Correct the extracted facts and choose the role you want to prepare for.</p></div><span className="profile-status">{profile.status}</span></section>
    {error && <p className="profile-alert profile-alert-error" role="alert">{error}</p>}
    {notice && <p className="profile-alert profile-alert-success" role="status">{notice}</p>}
    <fieldset disabled={busy} className="profile-card">
      <legend>Basic information</legend><div className="profile-grid">
        {[["fullName","Full name"],["professionalTitle","Professional title"],["targetRole","Target role"],["currentCompany","Current company"],["currentRole","Current role"]].map(([name,label]) =>
          <label className="profile-field" key={name}>{label}<input value={profile[name] || ""} onChange={e => field(name,e.target.value)} /></label>)}
        <label className="profile-field">Experience in months<input type="number" min="0" max="720" value={profile.totalExperienceMonths ?? 0} onChange={e => field("totalExperienceMonths",Number(e.target.value))} /></label>
        <label className="profile-field profile-field-full">Summary<textarea rows="5" value={profile.professionalSummary || ""} onChange={e => field("professionalSummary",e.target.value)} /></label>
      </div>
    </fieldset>
    <fieldset disabled={busy} className="profile-card"><legend>Skills</legend>
      {profile.skills.map((skill,index) => <div className="skill-card" key={index}><div className="profile-grid">
        <label className="profile-field">Skill<input value={skill.skillName || ""} onChange={e => item("skills",index,"skillName",e.target.value)} /></label>
        <label className="profile-field">Category<select value={skill.category || "OTHER"} onChange={e => item("skills",index,"category",e.target.value)}>{categories.map(c => <option key={c}>{c}</option>)}</select></label>
        <label className="profile-field">Proficiency<select value={skill.proficiency || ""} onChange={e => item("skills",index,"proficiency",e.target.value || null)}>
          <option value="">Unspecified</option>{["BEGINNER","INTERMEDIATE","ADVANCED","EXPERT"].map(c => <option key={c}>{c}</option>)}</select></label>
        <label className="profile-field">Years of experience<input type="number" min="0" max="60" step="0.5" value={skill.yearsOfExperience ?? ""} onChange={e => item("skills",index,"yearsOfExperience",e.target.value === "" ? null : Number(e.target.value))} /></label>
        <label className="profile-field profile-field-full">Evidence<textarea value={skill.evidence || ""} onChange={e => item("skills",index,"evidence",e.target.value)} /></label>
      </div><button className="profile-remove-button" onClick={() => field("skills",profile.skills.filter((_,i) => i !== index))}>Remove skill</button></div>)}
      <button className="profile-button profile-button-secondary" onClick={() => field("skills",[...profile.skills,{skillName:"",category:"OTHER",proficiency:null,source:"USER_ADDED",verified:false}])}>Add skill</button>
    </fieldset>
    <fieldset disabled={busy} className="profile-card"><legend>Projects</legend>
      {profile.projects.map((project,index) => <div className="project-card" key={index}><div className="profile-grid">
        {[["projectName","Project name"],["candidateRole","Your role"],["domain","Domain"]].map(([name,label]) => <label className="profile-field" key={name}>{label}<input value={project[name] || ""} onChange={e => item("projects",index,name,e.target.value)} /></label>)}
        {[["projectDescription","Description"],["responsibilities","Responsibilities"],["businessImpact","Business impact"]].map(([name,label]) => <label className="profile-field profile-field-full" key={name}>{label}<textarea value={project[name] || ""} onChange={e => item("projects",index,name,e.target.value)} /></label>)}
        <label className="profile-field profile-field-full">Technologies (comma separated)<input value={(project.technologies || []).map(t => t.technologyName).join(",")} onChange={e => item("projects",index,"technologies",e.target.value.split(",").map(name => ({technologyName:name,verified:false})))} /></label>
      </div><button className="profile-remove-button" onClick={() => field("projects",profile.projects.filter((_,i) => i !== index))}>Remove project</button></div>)}
      <button className="profile-button profile-button-secondary" onClick={() => field("projects",[...profile.projects,{projectName:"",projectDescription:"",candidateRole:"",technologies:[],currentProject:false}])}>Add project</button>
    </fieldset>
    <div className="profile-actions">
      <button disabled={busy} className="profile-button profile-button-secondary" onClick={() => save(false)}>Save changes</button>
      <button disabled={busy} className="profile-button profile-button-confirm" onClick={() => save(true)}>{busy ? "Saving..." : "Save and confirm"}</button>
      {profile.status === "CONFIRMED" && <button disabled={busy} className="profile-button profile-button-primary" onClick={() => navigate(`/question-bank/new?candidateProfileId=${profileId}`)}>Generate questions</button>}
    </div>
  </main>;
}
