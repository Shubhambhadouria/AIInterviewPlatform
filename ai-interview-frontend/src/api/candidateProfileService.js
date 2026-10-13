import api from "./axiosInstance";
export const getCandidateProfile = async (id) => (await api.get(`/candidate-profiles/${id}`)).data;
export const updateCandidateProfile = async (id, request) => (await api.put(`/candidate-profiles/${id}`, request)).data;
export const confirmCandidateProfile = async (id) => (await api.post(`/candidate-profiles/${id}/confirm`)).data;
