package com.aiinterviewcoach.modules.questionbank.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_sections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSection {
	@Id
	@GeneratedValue
	private UUID id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "question_bank_id", nullable = false)
	private QuestionBank questionBank;
	@Column(name = "section_name", nullable = false)
	private String name;
	@Column(name = "category")
	private String category;
	@Column(name = "display_order")
	private Integer displayOrder;
	@OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PreparationQuestion> questions = new ArrayList<>();

	public void addQuestion(PreparationQuestion question) {
		questions.add(question);
		question.setSection(this);
	}
}
