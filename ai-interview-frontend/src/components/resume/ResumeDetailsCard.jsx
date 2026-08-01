export default function ResumeDetailsCard({ uploadResponse, parseResponse }) {
  if (!uploadResponse && !parseResponse) return null;

  const profile = parseResponse?.candidateProfile ?? parseResponse?.profile;

  return (
    <section className="resume-card">
      <div className="resume-card__header">
        <div>
          <p className="eyebrow">Resume result</p>
          <h2>
            {profile?.fullName ||
              uploadResponse?.originalFileName ||
              "Uploaded resume"}
          </h2>
        </div>
        <span className="status-pill status-pill--success">
          {parseResponse ? "Parsed" : "Uploaded"}
        </span>
      </div>

      <dl className="resume-meta-grid">
        <Meta
          label="Resume ID"
          value={uploadResponse?.resumeId ?? parseResponse?.resumeId}
        />
        <Meta
          label="Profile ID"
          value={parseResponse?.candidateProfileId ?? profile?.id}
        />
        <Meta
          label="Professional title"
          value={profile?.professionalTitle ?? profile?.title}
        />
        <Meta
          label="Experience"
          value={formatExperience(
            profile?.totalExperienceMonths ?? profile?.experienceMonths,
          )}
        />
      </dl>

      {profile?.professionalSummary && (
        <div className="summary-block">
          <h3>Professional summary</h3>
          <p>{profile.professionalSummary}</p>
        </div>
      )}

      {Array.isArray(profile?.skills) && profile.skills.length > 0 && (
        <div className="summary-block">
          <h3>Detected skills</h3>
          <div className="chip-list">
            {profile.skills.map((skill) => (
              <span
                className="skill-chip"
                key={skill.id ?? skill.name ?? skill}
              >
                {skill.name ?? skill}
              </span>
            ))}
          </div>
        </div>
      )}
    </section>
  );
}

function Meta({ label, value }) {
  return (
    <div>
      <dt>{label}</dt>
      <dd>{value || "—"}</dd>
    </div>
  );
}

function formatExperience(months) {
  if (months === null || months === undefined) return "—";
  const years = Math.floor(months / 12);
  const remainingMonths = months % 12;
  return `${years} yr ${remainingMonths} mo`;
}
