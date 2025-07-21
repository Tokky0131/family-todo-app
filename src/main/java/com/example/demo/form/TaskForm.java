package com.example.demo.form;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskForm {

	@NotBlank(message = "タイトルは必須です。")
	@Size(max = 10, message = "タイトルは10文字以内にしてください。")
	private String title;

	@Size(max = 20, message = "詳細は20文字以内にしてください。")
	private String description;

	@NotBlank(message = "ステータスは必須です。")
	private String status; // たとえば "未完了" / "完了"

	@NotNull(message = "期限日は必須です。")
	private LocalDate deadline;
}
