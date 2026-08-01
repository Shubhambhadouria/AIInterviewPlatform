import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";

import {
  confirmCandidateProfile,
  getCandidateProfile,
  updateCandidateProfile,
} from "../api/candidateProfileService";

import "../styles/CandidateProfileReviewPage.css";

const emptyProfile = {
  fullName: "",
  email: "",
  title: "",
  totalExperienceMonths: 0,
  professionalSummary: "",
  status: "",
  skills: [],
  projects: [],
};

function CandidateProfileReviewPage() {
  const { profileId } = useParams();
  const navigate = useNavigate();

  const [profile, setProfile] = useState(emptyProfile);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [confirming, setConfirming] = useState(false);
  const [editing, setEditing] = useState(false);

  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    if (profileId) {
      loadCandidateProfile();
    }
  }, [profileId]);

  const loadCandidateProfile = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await getCandidateProfile(profileId);

      setProfile({
        ...emptyProfile,
        ...data,
        skills: data.skills || [],
        projects: data.projects || [],
      });
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to load the parsed candidate profile."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (event) => {
    const { name, value } = event.target;

    setProfile((previousProfile) => ({
      ...previousProfile,
      [name]:
        name === "totalExperienceMonths"
          ? Number(value)
          : value,
    }));
  };

  const handleSkillChange = (index, field, value) => {
    setProfile((previousProfile) => {
      const updatedSkills = [...previousProfile.skills];

      updatedSkills[index] = {
        ...updatedSkills[index],
        [field]:
          field === "yearsOfExperience"
            ? Number(value)
            : value,
      };

      return {
        ...previousProfile,
        skills: updatedSkills,
      };
    });
  };

  const addSkill = () => {
    setProfile((previousProfile) => ({
      ...previousProfile,
      skills: [
        ...previousProfile.skills,
        {
          name: "",
          category: "",
          proficiency: "",
          yearsOfExperience: 0,
          source: "USER_ADDED",
          evidence: "",
          verified: false,
        },
      ],
    }));
  };

  const removeSkill = (index) => {
    setProfile((previousProfile) => ({
      ...previousProfile,
      skills: previousProfile.skills.filter(
        (_, skillIndex) => skillIndex !== index
      ),
    }));
  };

  const handleProjectChange = (index, field, value) => {
    setProfile((previousProfile) => {
      const updatedProjects = [...previousProfile.projects];

      updatedProjects[index] = {
        ...updatedProjects[index],
        [field]: value,
      };

      return {
        ...previousProfile,
        projects: updatedProjects,
      };
    });
  };

  const addProject = () => {
    setProfile((previousProfile) => ({
      ...previousProfile,
      projects: [
        ...previousProfile.projects,
        {
          name: "",
          description: "",
          role: "",
          technologies: [],
        },
      ],
    }));
  };

  const removeProject = (index) => {
    setProfile((previousProfile) => ({
      ...previousProfile,
      projects: previousProfile.projects.filter(
        (_, projectIndex) => projectIndex !== index
      ),
    }));
  };

  const validateProfile = () => {
    if (!profile.fullName.trim()) {
      return "Full name is required.";
    }

    if (!profile.email.trim()) {
      return "Email is required.";
    }

    if (!profile.title.trim()) {
      return "Professional title is required.";
    }

    if (!profile.skills.length) {
      return "Add at least one skill before confirming the profile.";
    }

    const hasEmptySkill = profile.skills.some(
      (skill) => !skill.name?.trim()
    );

    if (hasEmptySkill) {
      return "Skill name cannot be empty.";
    }

    return "";
  };

  const buildUpdateRequest = () => ({
    fullName: profile.fullName.trim(),
    email: profile.email.trim(),
    title: profile.title.trim(),
    totalExperienceMonths:
      Number(profile.totalExperienceMonths) || 0,
    professionalSummary:
      profile.professionalSummary?.trim() || "",
    skills: profile.skills.map((skill) => ({
      id: skill.id || null,
      name: skill.name?.trim() || "",
      category: skill.category?.trim() || "",
      proficiency: skill.proficiency?.trim() || "",
      yearsOfExperience:
        Number(skill.yearsOfExperience) || 0,
      source: skill.source || "USER_ADDED",
      evidence: skill.evidence?.trim() || "",
      verified: Boolean(skill.verified),
    })),
    projects: profile.projects.map((project) => ({
      id: project.id || null,
      name: project.name?.trim() || "",
      description: project.description?.trim() || "",
      role: project.role?.trim() || "",
      technologies: project.technologies || [],
    })),
  });

  const handleSave = async () => {
    const validationError = validateProfile();

    if (validationError) {
      setError(validationError);
      return;
    }

    try {
      setSaving(true);
      setError("");
      setSuccessMessage("");

      const updatedProfile = await updateCandidateProfile(
        profileId,
        buildUpdateRequest()
      );

      setProfile({
        ...emptyProfile,
        ...updatedProfile,
        skills: updatedProfile.skills || [],
        projects: updatedProfile.projects || [],
      });

      setEditing(false);
      setSuccessMessage("Candidate profile updated successfully.");
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to update the candidate profile."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleConfirm = async () => {
    const validationError = validateProfile();

    if (validationError) {
      setError(validationError);
      return;
    }

    try {
      setConfirming(true);
      setError("");
      setSuccessMessage("");

      if (editing) {
        await updateCandidateProfile(
          profileId,
          buildUpdateRequest()
        );
      }

      const confirmedProfile =
        await confirmCandidateProfile(profileId);

      setProfile({
        ...emptyProfile,
        ...confirmedProfile,
        skills: confirmedProfile.skills || [],
        projects: confirmedProfile.projects || [],
      });

      setEditing(false);
      setSuccessMessage("Candidate profile confirmed successfully.");
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to confirm the candidate profile."
      );
    } finally {
      setConfirming(false);
    }
  };

  const handleGenerateQuestionBank = () => {
    navigate(
      `/question-bank/generate?profileId=${profileId}`
    );
  };

  if (loading) {
    return (
      <main className="profile-page">
        <div className="profile-state-card">
          <div className="profile-loader" />
          <p>Loading parsed profile...</p>
        </div>
      </main>
    );
  }

  return (
    <main className="profile-page">
      <section className="profile-header">
        <div>
          <p className="profile-eyebrow">
            Resume parsing completed
          </p>

          <h1>Review your candidate profile</h1>

          <p className="profile-header-description">
            Verify the extracted information and correct anything
            before generating interview questions.
          </p>
        </div>

        <span
          className={`profile-status profile-status-${profile.status?.toLowerCase()}`}
        >
          {profile.status || "REVIEW_REQUIRED"}
        </span>
      </section>

      {error && (
        <div className="profile-alert profile-alert-error">
          {error}
        </div>
      )}

      {successMessage && (
        <div className="profile-alert profile-alert-success">
          {successMessage}
        </div>
      )}

      <section className="profile-card">
        <div className="profile-card-heading">
          <div>
            <h2>Basic information</h2>
            <p>Information extracted from the uploaded resume.</p>
          </div>

          {profile.status !== "CONFIRMED" && !editing && (
            <button
              type="button"
              className="profile-button profile-button-secondary"
              onClick={() => {
                setEditing(true);
                setSuccessMessage("");
              }}
            >
              Edit profile
            </button>
          )}
        </div>

        <div className="profile-grid">
          <div className="profile-field">
            <label htmlFor="fullName">Full name</label>

            <input
              id="fullName"
              name="fullName"
              value={profile.fullName}
              onChange={handleInputChange}
              disabled={!editing}
              placeholder="Enter full name"
            />
          </div>

          <div className="profile-field">
            <label htmlFor="email">Email</label>

            <input
              id="email"
              name="email"
              type="email"
              value={profile.email}
              onChange={handleInputChange}
              disabled={!editing}
              placeholder="Enter email address"
            />
          </div>

          <div className="profile-field">
            <label htmlFor="title">Professional title</label>

            <input
              id="title"
              name="title"
              value={profile.title}
              onChange={handleInputChange}
              disabled={!editing}
              placeholder="Example: Java Backend Developer"
            />
          </div>

          <div className="profile-field">
            <label htmlFor="totalExperienceMonths">
              Total experience in months
            </label>

            <input
              id="totalExperienceMonths"
              name="totalExperienceMonths"
              type="number"
              min="0"
              value={profile.totalExperienceMonths}
              onChange={handleInputChange}
              disabled={!editing}
            />
          </div>

          <div className="profile-field profile-field-full">
            <label htmlFor="professionalSummary">
              Professional summary
            </label>

            <textarea
              id="professionalSummary"
              name="professionalSummary"
              value={profile.professionalSummary}
              onChange={handleInputChange}
              disabled={!editing}
              rows="6"
              placeholder="Enter professional summary"
            />
          </div>
        </div>
      </section>

      <section className="profile-card">
        <div className="profile-card-heading">
          <div>
            <h2>Skills</h2>
            <p>
              Review the skills that will be used to generate
              interview questions.
            </p>
          </div>

          {editing && (
            <button
              type="button"
              className="profile-button profile-button-secondary"
              onClick={addSkill}
            >
              + Add skill
            </button>
          )}
        </div>

        {!profile.skills.length ? (
          <div className="profile-empty-state">
            <p>No skills were extracted.</p>

            {editing && (
              <button
                type="button"
                className="profile-button profile-button-primary"
                onClick={addSkill}
              >
                Add first skill
              </button>
            )}
          </div>
        ) : (
          <div className="skill-list">
            {profile.skills.map((skill, index) => (
              <article
                className="skill-card"
                key={skill.id || `skill-${index}`}
              >
                <div className="skill-card-grid">
                  <div className="profile-field">
                    <label>Skill name</label>

                    <input
                      value={skill.name || ""}
                      onChange={(event) =>
                        handleSkillChange(
                          index,
                          "name",
                          event.target.value
                        )
                      }
                      disabled={!editing}
                      placeholder="Java"
                    />
                  </div>

                  <div className="profile-field">
                    <label>Category</label>

                    <input
                      value={skill.category || ""}
                      onChange={(event) =>
                        handleSkillChange(
                          index,
                          "category",
                          event.target.value
                        )
                      }
                      disabled={!editing}
                      placeholder="Backend"
                    />
                  </div>

                  <div className="profile-field">
                    <label>Proficiency</label>

                    <select
                      value={skill.proficiency || ""}
                      onChange={(event) =>
                        handleSkillChange(
                          index,
                          "proficiency",
                          event.target.value
                        )
                      }
                      disabled={!editing}
                    >
                      <option value="">Select proficiency</option>
                      <option value="BEGINNER">Beginner</option>
                      <option value="INTERMEDIATE">
                        Intermediate
                      </option>
                      <option value="ADVANCED">Advanced</option>
                      <option value="EXPERT">Expert</option>
                    </select>
                  </div>

                  <div className="profile-field">
                    <label>Years of experience</label>

                    <input
                      type="number"
                      min="0"
                      step="0.5"
                      value={skill.yearsOfExperience || 0}
                      onChange={(event) =>
                        handleSkillChange(
                          index,
                          "yearsOfExperience",
                          event.target.value
                        )
                      }
                      disabled={!editing}
                    />
                  </div>

                  <div className="profile-field profile-field-full">
                    <label>Evidence or description</label>

                    <textarea
                      rows="3"
                      value={skill.evidence || ""}
                      onChange={(event) =>
                        handleSkillChange(
                          index,
                          "evidence",
                          event.target.value
                        )
                      }
                      disabled={!editing}
                      placeholder="Where or how this skill was used"
                    />
                  </div>
                </div>

                {editing && (
                  <button
                    type="button"
                    className="profile-remove-button"
                    onClick={() => removeSkill(index)}
                  >
                    Remove skill
                  </button>
                )}
              </article>
            ))}
          </div>
        )}
      </section>

      <section className="profile-card">
        <div className="profile-card-heading">
          <div>
            <h2>Projects</h2>
            <p>
              Add or correct project details from the resume.
            </p>
          </div>

          {editing && (
            <button
              type="button"
              className="profile-button profile-button-secondary"
              onClick={addProject}
            >
              + Add project
            </button>
          )}
        </div>

        {!profile.projects.length ? (
          <div className="profile-empty-state">
            <p>No projects were extracted.</p>

            {editing && (
              <button
                type="button"
                className="profile-button profile-button-primary"
                onClick={addProject}
              >
                Add first project
              </button>
            )}
          </div>
        ) : (
          <div className="project-list">
            {profile.projects.map((project, index) => (
              <article
                className="project-card"
                key={project.id || `project-${index}`}
              >
                <div className="profile-grid">
                  <div className="profile-field">
                    <label>Project name</label>

                    <input
                      value={project.name || ""}
                      onChange={(event) =>
                        handleProjectChange(
                          index,
                          "name",
                          event.target.value
                        )
                      }
                      disabled={!editing}
                      placeholder="Project name"
                    />
                  </div>

                  <div className="profile-field">
                    <label>Your role</label>

                    <input
                      value={project.role || ""}
                      onChange={(event) =>
                        handleProjectChange(
                          index,
                          "role",
                          event.target.value
                        )
                      }
                      disabled={!editing}
                      placeholder="Backend Developer"
                    />
                  </div>

                  <div className="profile-field profile-field-full">
                    <label>Project description</label>

                    <textarea
                      rows="4"
                      value={project.description || ""}
                      onChange={(event) =>
                        handleProjectChange(
                          index,
                          "description",
                          event.target.value
                        )
                      }
                      disabled={!editing}
                      placeholder="Describe the project and your contribution"
                    />
                  </div>
                </div>

                {editing && (
                  <button
                    type="button"
                    className="profile-remove-button"
                    onClick={() => removeProject(index)}
                  >
                    Remove project
                  </button>
                )}
              </article>
            ))}
          </div>
        )}
      </section>

      <section className="profile-actions">
        {editing && (
          <>
            <button
              type="button"
              className="profile-button profile-button-secondary"
              onClick={() => {
                setEditing(false);
                loadCandidateProfile();
              }}
              disabled={saving || confirming}
            >
              Cancel
            </button>

            <button
              type="button"
              className="profile-button profile-button-primary"
              onClick={handleSave}
              disabled={saving || confirming}
            >
              {saving ? "Saving..." : "Save changes"}
            </button>
          </>
        )}

        {profile.status !== "CONFIRMED" ? (
          <button
            type="button"
            className="profile-button profile-button-confirm"
            onClick={handleConfirm}
            disabled={saving || confirming}
          >
            {confirming
              ? "Confirming..."
              : editing
                ? "Save and confirm profile"
                : "Confirm profile"}
          </button>
        ) : (
          <button
            type="button"
            className="profile-button profile-button-confirm"
            onClick={handleGenerateQuestionBank}
          >
            Generate question bank
          </button>
        )}
      </section>
    </main>
  );
}

export default CandidateProfileReviewPage;