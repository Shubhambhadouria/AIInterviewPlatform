import { useEffect, useState } from "react";
import { useParams } from "react-router";
import api from "../api/axiosInstance";
function PreparationQuestion({question,onChange}) {
  const [context,setContext] = useState("");
  const [busy,setBusy] = useState(false);
  const [error,setError] = useState("");
  async function update(path,method,payload) {
    setBusy(true);setError("");
    try {const {data} = await api[method](`/questions/${question.id}/${path}`,payload);onChange(data);if(path === "feedback") setContext("");}
    catch(err) {setError(err.response?.data?.message || err.message);} finally {setBusy(false);}
  }
  return <article className="question-card preparation-question"><h3>{question.questionText}</h3><p>{question.difficulty} · {question.resumeRelevance}</p>
    <div className="action-row"><button disabled={busy} className="button button-secondary" onClick={() => update("progress","patch",{bookmarked:!question.bookmarked})}>{question.bookmarked ? "Bookmarked" : "Bookmark"}</button>
      <label>Progress <select disabled={busy} value={question.progressStatus} onChange={e => update("progress","patch",{progressStatus:e.target.value})}>
        {["NOT_STARTED","IN_PROGRESS","COMPLETED"].map(status => <option key={status}>{status}</option>)}</select></label></div>
    {question.answer && <><details open><summary>30-second answer</summary><p className="preserve-lines">{question.answer.shortAnswer}</p></details>
      {Object.entries(question.answer).filter(([key,value]) => key !== "shortAnswer" && value).map(([key,value]) => <details key={key}><summary>{key.replace(/([A-Z])/g," $1")}</summary>{key === "codeExample" ? <pre>{value}</pre> : <p className="preserve-lines">{value}</p>}</details>)}</>}
    <form className="form" onSubmit={e => {e.preventDefault();update("feedback","put",{feedback:context});}}><label>Add project context or corrections<textarea rows="3" maxLength="5000" required value={context} onChange={e => setContext(e.target.value)} disabled={busy} /></label>
      <button className="button button-primary" disabled={busy || !context.trim()}>{busy ? "Saving..." : "Improve answer"}</button></form>
    {error && <p className="form-error" role="alert">{error}</p>}
  </article>;
}
export default function PreparationDetailPage() {
  const {bankId} = useParams();
  const [bank,setBank] = useState(null);
  const [error,setError] = useState("");
  const [filter,setFilter] = useState("");
  const [bookmarked,setBookmarked] = useState(false);
  useEffect(() => {let active=true;api.get(`/question-banks/${bankId}`).then(({data}) => {if(active) setBank(data);}).catch(err => {if(active) setError(err.response?.data?.message || err.message);});return () => {active=false;};},[bankId]);
  function replace(question) {setBank(current => ({...current,sections:current.sections.map(section => ({...section,questions:section.questions.map(q => q.id === question.id ? question : q)}))}));}
  return <main className="page-container"><h1>{bank?.title || "Question bank"}</h1>
    {error && <p className="form-error" role="alert">{error}</p>}{!bank && !error && <p>Loading...</p>}
    {bank && <><div className="preparation-filters"><label>Search questions<input value={filter} onChange={e => setFilter(e.target.value)} placeholder="Search by topic or question" /></label><label><input type="checkbox" checked={bookmarked} onChange={e => setBookmarked(e.target.checked)} /> Bookmarks only</label></div>
      {bank.sections.map(section => {const visible = section.questions.filter(q => (!bookmarked || q.bookmarked) && (section.name + " " + q.questionText).toLowerCase().includes(filter.toLowerCase()));return visible.length ? <section key={section.id}><h2>{section.name}</h2>{visible.map(q => <PreparationQuestion key={q.id} question={q} onChange={replace} />)}</section> : null;})}
      {!bank.sections.some(section => section.questions.some(q => (!bookmarked || q.bookmarked) && (section.name + " " + q.questionText).toLowerCase().includes(filter.toLowerCase()))) && <p>No questions match your filters.</p>}
    </>}
  </main>;
}
