package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.io.ByteArrayInputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import com.aiinterviewcoach.common.exception.AiProcessingException;
import com.aiinterviewcoach.modules.questionbank.entity.CandidateProfile;
import com.aiinterviewcoach.modules.questionbank.service.ResumeAiParser;

@Service
public class ResumeAiParserImpl implements ResumeAiParser {

	@Override
	public CandidateProfile parse(byte[] fileContent, String contentType) {

		if (fileContent == null || fileContent.length == 0) {
			throw new AiProcessingException("Resume file content is empty.");
		}

		String resumeText = extractText(fileContent, contentType);

		if (resumeText == null || resumeText.isBlank()) {
			throw new AiProcessingException("Could not extract readable text from the resume.");
		}

		return CandidateProfile.builder().professionalTitle(extractProfessionalTitle(resumeText))
				.professionalSummary(createSummary(resumeText))
				.totalExperienceMonths(extractExperienceMonths(resumeText)).build();
	}

	private String extractText(byte[] fileContent, String contentType) {

		if (contentType == null) {
			throw new AiProcessingException("Resume content type is missing.");
		}

		return switch (contentType.toLowerCase()) {
		case "application/pdf" -> extractPdfText(fileContent);

		case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> extractDocxText(fileContent);

		case "text/plain" -> new String(fileContent, java.nio.charset.StandardCharsets.UTF_8);

		default -> throw new AiProcessingException("Unsupported resume content type: " + contentType);
		};
	}

	private String extractPdfText(byte[] fileContent) {
		try (PDDocument document = Loader.loadPDF(fileContent)) {
			PDFTextStripper stripper = new PDFTextStripper();
			return stripper.getText(document);
		} catch (Exception exception) {
			throw new AiProcessingException("Failed to extract text from PDF resume.", exception);
		}
	}

	private String extractDocxText(byte[] fileContent) {
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);

				XWPFDocument document = new XWPFDocument(inputStream)) {
			StringBuilder text = new StringBuilder();

			document.getParagraphs().forEach(paragraph -> {
				if (!paragraph.getText().isBlank()) {
					text.append(paragraph.getText()).append("\n");
				}
			});

			return text.toString();

		} catch (Exception exception) {
			throw new AiProcessingException("Failed to extract text from DOCX resume.", exception);
		}
	}

	private String extractProfessionalTitle(String resumeText) {
		String lowerText = resumeText.toLowerCase();

		if (lowerText.contains("full stack developer")) {
			return "Full Stack Developer";
		}

		if (lowerText.contains("java developer")) {
			return "Java Developer";
		}

		if (lowerText.contains("backend developer")) {
			return "Backend Developer";
		}

		if (lowerText.contains("software engineer")) {
			return "Software Engineer";
		}

		return "Software Professional";
	}

	private Integer extractExperienceMonths(String resumeText) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern
				.compile("(\\d+(?:\\.\\d+)?)\\+?\\s*(?:years|year|yrs|yr)", java.util.regex.Pattern.CASE_INSENSITIVE);

		java.util.regex.Matcher matcher = pattern.matcher(resumeText);

		if (matcher.find()) {
			double years = Double.parseDouble(matcher.group(1));
			return (int) Math.round(years * 12);
		}

		return 0;
	}

	private String createSummary(String resumeText) {
		String normalizedText = resumeText.replaceAll("\\s+", " ").trim();

		int maximumLength = Math.min(normalizedText.length(), 500);

		return normalizedText.substring(0, maximumLength);
	}
}
