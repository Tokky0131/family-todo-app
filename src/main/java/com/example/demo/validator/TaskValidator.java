package com.example.demo.validator;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.form.TaskForm;

@Component
public class TaskValidator implements Validator {

	private final List<String> forbiddenWords = List.of("願う", "想う", "考える", "祈る", "成功", "合格", "トップ");

	@Override
	public boolean supports(Class<?> clazz) {
		return TaskForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		TaskForm form = (TaskForm) target;

		for (String word : forbiddenWords) {
			if ((form.getTitle() != null && form.getTitle().contains(word)) ||
					(form.getDescription() != null && form.getDescription().contains(word))) {
				errors.rejectValue("title", "forbidden.word", "もっと具体的に！");
				break;
			}
		}
	}
}
