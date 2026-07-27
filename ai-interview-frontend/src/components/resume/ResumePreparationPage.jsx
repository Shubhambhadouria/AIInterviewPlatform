import { useState } from "react";
import { useNavigate } from "react-router";
import { parseResume, uploadResume } from "../../api/resumeApi";
import ResumeDropzone from "../../components/resume/ResumeDropzone";
import ResumeDetailsCard from "../../components/resume/ResumeDetailsCard";
import ParsingStatus from "../../components/resume/ParsingStatus";
import "../../styles/resume-preparation.css";

export default function ResumePreparationPage() {
  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [step, setStep] = useState("idle");
  const [uploadResponse, setUploadResponse] = useState(null);
  const [parseResponse, setParseResponse] = useState(null);
  const [error, setError] = useState("");

  const busy = step === "uploading" || step === "parsing";
  const resumeId = uploadResponse?.resumeId ?? uploadResponse?.id;
  const candidateProfileId =
    parseResponse?.candidateProfileId ??
    parseResponse?.candidateProfile?.id ??
    parseResponse?.profile?.id;

  const handleUpload = async () => {
    if (!file) {
      setError("Select a resume before uploading.");
      return;
    }

    try {
      setError("");
      setParseResponse(null);
      setStep("uploading");
      const response = await uploadResume(file);
      setUploadResponse(response);
      setStep("uploaded");
    } catch (requestError) {
      setStep("idle");
      setError(extractApiError(requestError));
    }
  };

  const handleParse = async () => {
    if (!resumeId) {
      setError("Upload the resume first.");
      return;
    }

    try {
      setError("");
      setStep("parsing");
      const response = await parseResume(resumeId);
      setParseResponse(response);
      setStep("parsed");
      navigate(`/candidate-profiles/${profileId}/review`);
    } catch (requestError) {
      setStep("uploaded");
      setError(extractApiError(requestError));
    }
  };

  const reset = () => {
    setFile(null);
    setStep("idle");
    setUploadResponse(null);
    setParseResponse(null);
    setError("");
  };

  return (
    <main className="resume-page">
      <section className="resume-hero">
        <p className="eyebrow">AI interview preparation</p>
        <h1>Build a question bank from your resume</h1>
        <p>
          Upload your resume, let the backend extract your skills and projects,
          and then generate project-focused interview questions.
        </p>
      </section>

      <section className="resume-card">
        <div className="resume-card__header">
          <div>
            <p className="eyebrow">Step 1</p>
            <h2>Upload resume</h2>
          </div>
          {file && (
            <button
              type="button"
              className="button button--ghost"
              onClick={reset}
              disabled={busy}
            >
              Reset
            </button>
          )}
        </div>

        <ResumeDropzone file={file} onFileSelect={setFile} disabled={busy} />

        <div className="action-row">
          <button
            type="button"
            className="button button--primary"
            onClick={handleUpload}
            disabled={!file || busy}
          >
            {step === "uploading" ? "Uploading…" : "Upload resume"}
          </button>

          <button
            type="button"
            className="button button--secondary"
            onClick={handleParse}
            disabled={!resumeId || busy || step === "parsed"}
          >
            {step === "parsing" ? "Parsing…" : "Parse with AI"}
          </button>
        </div>

        <ParsingStatus step={step} error={error} />
      </section>

      <ResumeDetailsCard
        uploadResponse={uploadResponse}
        parseResponse={parseResponse}
      />

      {candidateProfileId && (
        <section className="next-step-card">
          <div>
            <p className="eyebrow">Step 2</p>
            <h2>Generate interview questions</h2>
            <p>
              Use profile ID: <code>{candidateProfileId}</code>
            </p>
          </div>
          <button
            type="button"
            className="button button--primary"
            onClick={() =>
              navigate(
                `/question-bank/new?candidateProfileId=${candidateProfileId}`,
              )
            }
          >
            Continue
          </button>
        </section>
      )}
    </main>
  );
}

function extractApiError(error) {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.error ||
    error?.message ||
    "Something went wrong. Please try again."
  );
}
