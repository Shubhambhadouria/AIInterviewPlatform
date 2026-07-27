export default function QuestionBankSummaryCard({ questionBank }) {
  if (!questionBank) return null;

  return (
    <section className="resume-card">
      <div className="resume-card__header">
        <div>
          <p className="eyebrow">Question bank</p>
          <h2>{questionBank.title || "Interview preparation bank"}</h2>
        </div>
        <span className="status-pill">{questionBank.status || "Created"}</span>
      </div>

      <dl className="resume-meta-grid">
        <Meta
          label="Question bank ID"
          value={questionBank.questionBankId ?? questionBank.id}
        />
        <Meta label="Profile ID" value={questionBank.candidateProfileId} />
        <Meta label="Sections" value={questionBank.totalSections} />
        <Meta label="Questions" value={questionBank.totalQuestions} />
      </dl>

      {questionBank.message && (
        <p className="muted-text">{questionBank.message}</p>
      )}
    </section>
  );
}

function Meta({ label, value }) {
  return (
    <div>
      <dt>{label}</dt>
      <dd>{value ?? "—"}</dd>
    </div>
  );
}
