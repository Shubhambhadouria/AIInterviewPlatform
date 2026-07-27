import axiosInstance from "./axiosInstance";

/**
 * Adjust the URL/body to match GenerateQuestionBankRequest in your backend.
 */
export async function generateQuestionBank(payload) {
  const { data } = await axiosInstance.post("/question-banks/generate", payload);
  return data;
}

export async function getQuestionBank(questionBankId) {
  const { data } = await axiosInstance.get(`/question-banks/${questionBankId}`);
  return data;
}

export async function getQuestionBankQuestions(questionBankId) {
  const { data } = await axiosInstance.get(
    `/question-banks/${questionBankId}/questions`
  );
  return data;
}
