package com.aiinterviewcoach.modules.questionbank.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import com.aiinterviewcoach.common.exception.BadRequestException;
import com.aiinterviewcoach.modules.questionbank.entity.Resume;
import com.aiinterviewcoach.modules.questionbank.exception.FileStorageException;
import com.aiinterviewcoach.modules.questionbank.service.FileStorageService;
import com.aiinterviewcoach.modules.questionbank.service.ResumeTextExtractor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeTextExtractorImpl implements ResumeTextExtractor {

	private static final String PDF = "application/pdf";
	private static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	private static final int MINIMUM_TEXT_LENGTH = 50;

	private final FileStorageService fileStorageService;

	@Override
	public String extract(Resume resume) {
		byte[] fileContent = fileStorageService.read(resume.getStorageKey());
		if (fileContent.length == 0)
			throw new FileStorageException("Stored resume file is empty");

		try {
			String extractedText = switch (resume.getContentType()) {
			case PDF -> extractPdf(fileContent);
			case DOCX -> extractDocx(fileContent);
			default -> throw new BadRequestException("Unsupported resume content type: " + resume.getContentType());
			};
			String cleanedText = cleanText(extractedText);
			if (cleanedText.length() < MINIMUM_TEXT_LENGTH) {
				throw new BadRequestException(
						"Unable to extract sufficient text. The resume may be image-based or scanned.");
			}
			return cleanedText;
		} catch (IOException exception) {
			throw new FileStorageException("Failed to extract resume text", exception);
		}
	}

	private String extractPdf(byte[] bytes) throws IOException {
		try (PDDocument document = Loader.loadPDF(bytes)) {
			return new PDFTextStripper().getText(document);
		}
	}

	private String extractDocx(byte[] bytes) throws IOException {
		try (ByteArrayInputStream input = new ByteArrayInputStream(bytes);
				XWPFDocument document = new XWPFDocument(input);
				XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
			return extractor.getText();
		}
	}

	private String cleanText(String text) {
		if (text == null)
			return "";
		return text.replace('\u0000', ' ').replaceAll("[\\t\\x0B\\f\\r]+", " ").replaceAll(" +", " ")
				.replaceAll("\\n{3,}", "\\n\\n").trim();
	}
}
