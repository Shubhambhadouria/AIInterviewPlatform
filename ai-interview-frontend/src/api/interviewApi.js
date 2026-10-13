import api from "./axiosInstance";
async function request(method,path,payload) {
  try {return (await api[method](path,payload)).data;}
  catch(err) {throw new Error(err.response?.data?.message || err.message || "Interview request failed.", {cause:err});}
}
export const startInterview = payload => request("post","/interviews/start",payload);
export const submitInterviewAnswer = (id,payload) => request("post",`/interviews/${id}/answer`,payload);
export const submitAnswer = submitInterviewAnswer;
export const getInterviewResult = id => request("get",`/interviews/${id}/result`);
export const getResult = getInterviewResult;
export const getInterviewHistory = () => request("get","/interviews/history");
export const getInterviewSession = id => request("get",`/interviews/${id}`);
