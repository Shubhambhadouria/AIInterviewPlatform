import axiosInstance from "./axiosInstance";

/**
 * Uploads a PDF/DOC/DOCX resume.
 * Expected backend: POST /api/resumes/upload (multipart field name: file)
 */
export async function uploadResume(file) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await axiosInstance.post("/resumes/upload", formData);

  return response.data;
}

/**
 * Starts/parses an uploaded resume.
 * Expected backend: POST /api/resumes/{resumeId}/parse
 */
export async function parseResume(resumeId) {
  const { data } = await axiosInstance.post(`/resumes/${resumeId}/parse`);
  return data;
}

/**
 * Optional endpoint. Keep this method only when the backend exposes it.
 * Expected backend: GET /api/resumes/{resumeId}
 */
export async function getResume(resumeId) {
  const { data } = await axiosInstance.get(`/resumes/${resumeId}`);
  return data;
}
