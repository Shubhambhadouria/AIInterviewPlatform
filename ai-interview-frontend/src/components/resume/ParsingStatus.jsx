export default function ParsingStatus({ step, error }) {
  if (error) {
    return (
      <div className="alert alert--error" role="alert">
        <strong>Request failed</strong>
        <span>{error}</span>
      </div>
    );
  }

  if (step === "idle") return null;

  const messages = {
    uploading: "Uploading your resume…",
    uploaded: "Resume uploaded successfully.",
    parsing: "AI is extracting your profile, skills, and projects…",
    parsed: "Resume parsed successfully.",
  };

  return (
    <div
      className={`alert ${step === "uploaded" || step === "parsed" ? "alert--success" : "alert--info"}`}
    >
      {(step === "uploading" || step === "parsing") && (
        <span className="spinner" />
      )}
      <span>{messages[step]}</span>
    </div>
  );
}
