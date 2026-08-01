import axiosInstance from "./axiosInstance";

const CANDIDATE_PROFILE_API = "/candidate-profiles";

const validateProfileId = (profileId) => {
  if (!profileId) {
    throw new Error("Candidate profile ID is required.");
  }
};

export const getCandidateProfile = async (profileId) => {
  validateProfileId(profileId);

  const { data } = await axiosInstance.get(
    `${CANDIDATE_PROFILE_API}/${profileId}`
  );

  return data;
};

export const updateCandidateProfile = async (profileId, request) => {
  validateProfileId(profileId);

  if (!request) {
    throw new Error("Candidate profile update request is required.");
  }

  const { data } = await axiosInstance.put(
    `${CANDIDATE_PROFILE_API}/${profileId}`,
    request
  );

  return data;
};

export const confirmCandidateProfile = async (profileId) => {
  validateProfileId(profileId);

  const { data } = await axiosInstance.patch(
    `${CANDIDATE_PROFILE_API}/${profileId}/confirm`
  );

  return data;
};