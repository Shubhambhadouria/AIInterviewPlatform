import { useRef, useState } from "react";

const ACCEPTED_TYPES = [
  "application/pdf",
  "application/msword",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
];

const MAX_FILE_SIZE = 5 * 1024 * 1024;

export default function ResumeDropzone({ file, onFileSelect, disabled = false }) {
  const inputRef = useRef(null);
  const [dragging, setDragging] = useState(false);
  const [validationError, setValidationError] = useState("");

  const validateAndSelect = (selectedFile) => {
    setValidationError("");

    if (!selectedFile) return;

    if (!ACCEPTED_TYPES.includes(selectedFile.type)) {
      setValidationError("Upload a PDF, DOC, or DOCX file.");
      return;
    }

    if (selectedFile.size > MAX_FILE_SIZE) {
      setValidationError("The resume must be 5 MB or smaller.");
      return;
    }

    onFileSelect(selectedFile);
  };

  const handleDrop = (event) => {
    event.preventDefault();
    setDragging(false);
    if (!disabled) validateAndSelect(event.dataTransfer.files?.[0]);
  };

  return (
<div>
<button
        type="button"
        className={`resume-dropzone ${dragging ? "resume-dropzone--dragging" : ""}`}
        onClick={() => !disabled && inputRef.current?.click()}
        onDragOver={(event) => {
          event.preventDefault();
          if (!disabled) setDragging(true);
        }}
        onDragLeave={() => setDragging(false)}
        onDrop={handleDrop}
        disabled={disabled}
>
<span className="resume-dropzone__icon">↑</span>
<strong>{file ? file.name : "Drop your resume here"}</strong>
<span>{file ? formatBytes(file.size) : "or click to browse • PDF, DOC, DOCX • max 5 MB"}</span>
</button>

<input
        ref={inputRef}
        type="file"
        hidden
        accept=".pdf,.doc,.docx"
        onChange={(event) => validateAndSelect(event.target.files?.[0])}
      />

      {validationError &&<p className="form-error">{validationError}</p>}
</div>
  );
}

function formatBytes(bytes) {
  if (!bytes) return "0 KB";
  return `${(bytes / 1024).toFixed(1)} KB`;
}
