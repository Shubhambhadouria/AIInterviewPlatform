create index idx_interview_sessions_user_created on interview_sessions (user_id, created_at desc);
create index idx_interview_questions_session_order on interview_questions (interview_session_id, display_order, created_at);
create index idx_resumes_user_created on resumes (user_id, created_at desc);
create index idx_question_banks_user_created on question_banks (user_id, created_at desc);
create index idx_question_sections_bank on question_sections (question_bank_id);
create index idx_preparation_questions_section on preparation_questions (section_id);
create index idx_candidate_skills_profile on candidate_skills (candidate_profile_id);
create index idx_candidate_projects_profile on candidate_projects (candidate_profile_id);
create index idx_project_technologies_project on project_technologies (candidate_project_id);
