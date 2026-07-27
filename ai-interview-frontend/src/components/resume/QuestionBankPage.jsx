import { useMemo, useState } from "react";
import { useSearchParams } from "react-router";
import { generateQuestionBank } from "../../api/questionBankApi";
import QuestionBankSummaryCard from "../../components/resume/QuestionBankSummaryCard";
import "../../styles/resume-preparation.css";

const DEFAULT_SECTIONS = [
  "Java",
  "Spring Boot",
  "Microservices",
  "SQL",
  "React",
  "AWS",
];

export default function QuestionBankPage() {
  const [searchParams] = useSearchParams();
  const initialProfileId = searchParams.get("candidateProfileId") || "";
  const [candidateProfileId, setCandidateProfileId] =
    useState(initialProfileId);
  const [title, setTitle] = useState("Resume-based interview preparation");
  const [selectedSections, setSelectedSections] = useState([
    "Java",
    "Spring Boot",
    "SQL",
  ]);
  const [questionCount, setQuestionCount] = useState(30);
  const [questionBank, setQuestionBank] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const canSubmit = useMemo(
    () =>
      candidateProfileId.trim() &&
      selectedSections.length > 0 &&
      questionCount > 0,
    [candidateProfileId, selectedSections, questionCount],
  );

  const toggleSection = (section) => {
    setSelectedSections((current) =>
      current.includes(section)
        ? current.filter((item) => item !== section)
        : [...current, section],
    );
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      setLoading(true);
      setError("");

      // Rename these fields if your GenerateQuestionBankRequest uses different names.
      const response = await generateQuestionBank({
        candidateProfileId,
        title,
        categories: selectedSections,
        totalQuestions: Number(questionCount),
      });

      setQuestionBank(response);
    } catch (requestError) {
      setError(
        requestError?.response?.data?.message ||
          requestError?.message ||
          "Question bank generation failed.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="resume-page">
      <section className="resume-hero">
        <p className="eyebrow">Question bank generator</p>
        <h1>Create role- and project-focused questions</h1>
        <p>
          Select the sections you want to prepare and submit the parsed profile
          ID.
        </p>
      </section>

      <form className="resume-card form-stack" onSubmit={handleSubmit}>
        <label>
          Candidate profile ID
          <input
            value={candidateProfileId}
            onChange={(event) => setCandidateProfileId(event.target.value)}
            placeholder="UUID returned by resume parsing"
            required
          />
        </label>

        <label>
          Question bank title
          <input
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            required
          />
        </label>

        <div>
          <span className="field-label">Sections</span>
          <div className="checkbox-grid">
            {DEFAULT_SECTIONS.map((section) => (
              <label className="checkbox-card" key={section}>
                <input
                  type="checkbox"
                  checked={selectedSections.includes(section)}
                  onChange={() => toggleSection(section)}
                />
                <span>{section}</span>
              </label>
            ))}
          </div>
        </div>

        <label>
          Total questions
          <input
            type="number"
            min="1"
            max="100"
            value={questionCount}
            onChange={(event) => setQuestionCount(event.target.value)}
          />
        </label>

        {error && <div className="alert alert--error">{error}</div>}

        <button
          className="button button--primary"
          type="submit"
          disabled={!canSubmit || loading}
        >
          {loading ? "Generating…" : "Generate question bank"}
        </button>
      </form>

      <QuestionBankSummaryCard questionBank={questionBank} />
    </main>
  );
}
