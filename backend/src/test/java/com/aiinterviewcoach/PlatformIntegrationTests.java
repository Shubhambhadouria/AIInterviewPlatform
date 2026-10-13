package com.aiinterviewcoach;

import java.net.URI;
import java.net.http.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import com.aiinterviewcoach.modules.ai.service.GeminiClient;
import com.aiinterviewcoach.modules.ai.dto.AiEvaluationResponse;
import com.aiinterviewcoach.modules.interview.service.impl.InterviewServiceImpl.GeneratedQuestions;
import com.aiinterviewcoach.modules.questionbank.service.impl.ResumeAiParserImpl.ExtractedProfile;
import com.aiinterviewcoach.modules.questionbank.dto.*;
import com.aiinterviewcoach.modules.questionbank.enums.*;
import com.aiinterviewcoach.common.exception.AiProcessingException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PlatformIntegrationTests {
    @LocalServerPort int port;
    @MockitoBean GeminiClient ai;
    final HttpClient http = HttpClient.newHttpClient();
    final JsonMapper json = JsonMapper.builder().build();
    final GeneratedPreparationAnswer answer = new GeneratedPreparationAnswer("Short answer", "Detailed answer",
        "Internal working", "Example", "Usage", "STAR", "Impact", "Follow-ups", "Mistakes", "Avoid", "Product", "Service");

    @BeforeEach void configureAi() {
        when(ai.generate(anyString(), eq(GeneratedQuestions.class))).thenReturn(new GeneratedQuestions(List.of("Explain Java collections", "Explain transactions")));
        when(ai.generate(anyString(), eq(AiEvaluationResponse.class))).thenReturn(new AiEvaluationResponse(8, "Good explanation; add a concrete example.", "An improved answer"));
        when(ai.generate(anyString(), eq(ExtractedProfile.class))).thenReturn(new ExtractedProfile("Candidate", null,
            "Backend Developer", 24, "Java developer", null, null,
            List.of(new CandidateSkillRequest("Java", SkillCategory.PROGRAMMING_LANGUAGE, SkillProficiency.INTERMEDIATE,
                null, SkillSource.RESUME, "Built Java services", false)), List.of()));
        when(ai.generate(anyString(), eq(GeneratedQuestionBank.class))).thenReturn(new GeneratedQuestionBank("Java",
            List.of(new GeneratedQuestionSection("Java", "BACKEND", 1, List.of(new GeneratedPreparationQuestion(
                "How do transactions work?", "INTERMEDIATE", "TECHNICAL", "Java", null, "Java services", answer))))));
        when(ai.generate(anyString(), eq(GeneratedPreparationAnswer.class))).thenReturn(answer);
    }

    @Test void authValidationAndInvalidTokens() throws Exception {
        assertEquals(401, call("GET", "/auth/me", null, null).statusCode());
        assertEquals(200, http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/actuator/health")).GET().build(), HttpResponse.BodyHandlers.ofString()).statusCode());
        assertEquals(401, call("GET", "/auth/me", "invalid-token", null).statusCode());
        assertEquals(400, call("POST", "/auth/register", null, Map.of("fullName", "", "email", "bad", "password", "x")).statusCode());
        String email = UUID.randomUUID() + "@example.com";
        var payload = Map.of("fullName", "Candidate", "email", email, "password", "TestPassword123!");
        var registered = call("POST", "/auth/register", null, payload);
        assertEquals(200, registered.statusCode());
        assertEquals(400, call("POST", "/auth/register", null, payload).statusCode());
        assertEquals(200, call("POST", "/auth/login", null, Map.of("email", email.toUpperCase(), "password", "TestPassword123!")).statusCode());
        assertEquals(400, call("POST", "/auth/login", null, Map.of("email", email, "password", "incorrect")).statusCode());
    }

    @Test void interviewProgressionResumeAndOwnership() throws Exception {
        String token = register();
        String other = register();
        var started = body(ok(call("POST", "/interviews/start", token,
            Map.of("topic", "Java", "targetRole", "Developer", "experienceLevel", "INTERMEDIATE", "totalQuestions", 2))));
        String id = started.path("sessionId").asText();
        String question = started.path("questions").path(0).path("questionId").asText();
        assertEquals(403, call("GET", "/interviews/" + id, other, null).statusCode());
        var submitted = body(ok(call("POST", "/interviews/" + id + "/answer", token, Map.of("questionId", question, "answer", "A useful answer"))));
        assertFalse(submitted.path("completed").asBoolean());
        assertEquals(1, submitted.path("currentQuestionNumber").asInt());
        assertEquals(400, call("POST", "/interviews/" + id + "/answer", token, Map.of("questionId", question, "answer", "Duplicate")).statusCode());
        var resumed = body(ok(call("GET", "/interviews/" + id, token, null)));
        assertEquals(1, resumed.path("answeredQuestions").asInt());
        assertEquals(1, resumed.path("questions").size());
        String next = resumed.path("questions").path(0).path("questionId").asText();
        var completed = body(ok(call("POST", "/interviews/" + id + "/answer", token, Map.of("questionId", next, "answer", "Second answer"))));
        assertTrue(completed.path("completed").asBoolean());
        var result = body(ok(call("GET", "/interviews/" + id + "/result", token, null)));
        assertEquals("COMPLETED", result.path("status").asText());
        assertEquals(8, result.path("overallScore").asInt());
        assertEquals("An improved answer", result.path("questions").path(0).path("improvedAnswer").asText());
        assertEquals(1, body(ok(call("GET", "/interviews/history", token, null))).size());
    }

    @Test void resumeReviewGenerationRevisionAndProgress() throws Exception {
        String token = register();
        String other = register();
        String resume = upload(token);
        String profile = body(ok(call("POST", "/resumes/" + resume + "/parse", token, Map.of()))).path("candidateProfileId").asText();
        var reviewed = body(ok(call("GET", "/candidate-profiles/" + profile, token, null)));
        assertEquals("Java", reviewed.path("skills").path(0).path("skillName").asText());
        assertEquals(404, call("GET", "/candidate-profiles/" + profile, other, null).statusCode());
        assertEquals(400, call("POST", "/question-banks/generate", token, Map.of("candidateProfileId", profile)).statusCode());
        ok(call("POST", "/candidate-profiles/" + profile + "/confirm", token, Map.of()));
        var generated = body(ok(call("POST", "/question-banks/generate", token, Map.of("candidateProfileId", profile))));
        String bank = generated.path("questionBankId").asText();
        var detail = body(ok(call("GET", "/question-banks/" + bank, token, null)));
        assertEquals("READY", detail.path("status").asText());
        var question = detail.path("sections").path(0).path("questions").path(0);
        assertEquals("Detailed answer", question.path("answer").path("detailedAnswer").asText());
        String questionId = question.path("id").asText();
        assertEquals(404, call("GET", "/question-banks/" + bank, other, null).statusCode());
        assertEquals(404, call("PUT", "/questions/" + questionId + "/feedback", other, Map.of("feedback", "My context")).statusCode());
        ok(call("PUT", "/questions/" + questionId + "/feedback", token, Map.of("feedback", "I use PostgreSQL transactions")));
        ok(call("PATCH", "/questions/" + questionId + "/progress", token, Map.of("bookmarked", true, "progressStatus", "COMPLETED")));
        var saved = body(ok(call("GET", "/question-banks/" + bank, token, null))).path("sections").path(0).path("questions").path(0);
        assertTrue(saved.path("bookmarked").asBoolean());
        assertEquals("COMPLETED", saved.path("progressStatus").asText());
        assertEquals(1, body(ok(call("GET", "/question-banks", token, null))).size());
    }

    @Test void failedAiRequestsAreRecoverable() throws Exception {
        String token = register(); String resume = upload(token);
        when(ai.generate(anyString(), eq(ExtractedProfile.class))).thenThrow(new AiProcessingException("Unavailable"));
        assertEquals(502, call("POST", "/resumes/" + resume + "/parse", token, Map.of()).statusCode());
        configureAi();
        String profile = body(ok(call("POST", "/resumes/" + resume + "/parse", token, Map.of()))).path("candidateProfileId").asText();
        ok(call("POST", "/candidate-profiles/" + profile + "/confirm", token, Map.of()));
        when(ai.generate(anyString(), eq(GeneratedQuestionBank.class))).thenThrow(new AiProcessingException("Unavailable"));
        assertEquals(502, call("POST", "/question-banks/generate", token, Map.of("candidateProfileId", profile)).statusCode());
        var banks = body(ok(call("GET", "/question-banks", token, null)));
        assertEquals("GENERATION_FAILED", banks.path(0).path("status").asText());
        configureAi();
        ok(call("POST", "/question-banks/generate", token, Map.of("candidateProfileId", profile)));
    }

    String register() throws Exception {
        return body(ok(call("POST", "/auth/register", null, Map.of("fullName", "Candidate", "email",
            UUID.randomUUID() + "@example.com", "password", "TestPassword123!")))).path("token").asText();
    }
    HttpResponse<String> call(String method, String path, String token, Object payload) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api" + path));
        if (token != null) request.header("Authorization", "Bearer " + token);
        request.header("Content-Type", "application/json");
        request.method(method, payload == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload)));
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
    HttpResponse<String> ok(HttpResponse<String> response) {
        assertTrue(response.statusCode() == 200 || response.statusCode() == 201, response.body()); return response;
    }
    JsonNode body(HttpResponse<String> response) { return json.readTree(response.body()); }
    String upload(String token) throws Exception {
        byte[] file;
        try (var document = new XWPFDocument(); var output = new ByteArrayOutputStream()) {
            document.createParagraph().createRun().setText("Candidate, Java Backend Developer. Two years building Java and PostgreSQL services.");
            document.write(output); file = output.toByteArray();
        }
        String boundary = "testBoundary";
        var output = new ByteArrayOutputStream();
        output.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"resume.docx\"\r\nContent-Type: application/vnd.openxmlformats-officedocument.wordprocessingml.document\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        output.write(file); output.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/resumes/upload"))
            .header("Authorization", "Bearer " + token).header("Content-Type", "multipart/form-data; boundary=" + boundary)
            .POST(HttpRequest.BodyPublishers.ofByteArray(output.toByteArray())).build();
        var response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), response.body());
        return body(response).path("resumeId").asText();
    }
}
