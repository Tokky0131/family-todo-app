package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;   // ★ 追加
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Task;
import com.example.demo.service.TaskService;

import jakarta.validation.Valid;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public String tasks(Model model) {
        // 期限順で取得
        model.addAttribute("tasks", taskService.findAllOrderByDeadline());
        return "tasks";
    }

    @GetMapping("/tasks/create")
    public String createForm(Model model) {
        model.addAttribute("task", new Task());
        return "create-task";
    }

    @PostMapping("/tasks/create")
    public String confirm(
        @Valid @ModelAttribute Task task,
        BindingResult bindingResult,
        Model model
    ) {
        // ⭐ タイトル＋詳細の重複チェック
        if (taskService.existsByTitleAndDescription(task.getTitle(), task.getDescription())) {
            bindingResult.rejectValue("title", "duplicate", "タイトルと詳細が同じタスクが既に存在します。");
        }

        // ⭐ タイトル未入力 or 長さオーバー
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            bindingResult.rejectValue("title", "blank", "タイトルは必須です。");
        } else if (task.getTitle().length() > 10) {
            bindingResult.rejectValue("title", "length", "タイトルは10文字以内で入力してください。");
        }

        // ⭐ 詳細長さオーバー
        if (task.getDescription() != null && !task.getDescription().isBlank()) {
            if (task.getDescription().length() > 20) {
                bindingResult.rejectValue("description", "length", "詳細は20文字以内で入力してください。");
            }
        }

        // ⭐ 禁止ワード（抽象＋結果）を含むか？
        List<String> forbiddenWords = List.of(
            "願う", "想う", "考える", "祈る",
            "優勝", "合格", "達成", "成功", "勝つ",
            "負けない", "100点", "1位", "トップ", "制覇"
        );
        for (String word : forbiddenWords) {
            if (task.getTitle() != null && task.getTitle().contains(word)) {
                bindingResult.rejectValue("title", "invalid.word", "もっと具体的に！");
                break;
            }
        }

        // ⭐ 進捗が未入力なら未着手
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("未着手");
        }

        if (bindingResult.hasErrors()) {
            return "create-task";
        }

        model.addAttribute("task", task);
        return "confirm-task";
    }

    @PostMapping("/tasks/complete")
    public String complete(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        // 念のためステータス未入力時の補完
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("未着手");
        }

        taskService.save(task);

        redirectAttributes.addFlashAttribute("successMessage", "登録が完了しました！");
        return "redirect:/tasks";
    }

    // ===== 編集・削除（既存のまま） =====
    @GetMapping("/tasks/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id);
        model.addAttribute("task", task);
        return "edit-task";
    }

    @PostMapping("/tasks/edit/confirm")
    public String editConfirm(
        @Valid @ModelAttribute Task task,
        BindingResult bindingResult,
        Model model
    ) {
        // ✅ NGワードチェック
        List<String> forbiddenWords = Arrays.asList("願う", "想う", "考える", "祈る", "達成", "成功", "獲得");
        boolean forbidden = forbiddenWords.stream().anyMatch(word ->
            (task.getTitle() != null && task.getTitle().contains(word)) ||
            (task.getDescription() != null && task.getDescription().contains(word))
        );
        if (forbidden) {
            bindingResult.rejectValue("title", "forbidden", "もっと具体的に！");
        }

        // ✅ タイトル10文字以内
        if (task.getTitle() != null && task.getTitle().length() > 10) {
            bindingResult.rejectValue("title", "length", "タイトルは10文字以内にしてください。");
        }

        // ✅ 詳細20文字以内
        if (task.getDescription() != null && task.getDescription().length() > 20) {
            bindingResult.rejectValue("description", "length", "詳細は20文字以内にしてください。");
        }

        // ✅ タイトル＋詳細が完全一致するタスクはNG（自分自身は除外）
        boolean exists = taskService.existsByTitleAndDescription(task.getTitle(), task.getDescription());
        Task existingTask = taskService.findById(task.getId());
        boolean isSameTask = existingTask != null
            && existingTask.getTitle().equals(task.getTitle())
            && existingTask.getDescription().equals(task.getDescription());
        if (exists && !isSameTask) {
            bindingResult.rejectValue("title", "duplicate", "タイトルと詳細が同じタスクが既に存在します。");
        }

        // ✅ 進捗が未入力ならデフォルト
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("未着手");
        }

        if (bindingResult.hasErrors()) {
            return "edit-task"; // エラーがあれば入力画面へ戻る
        }

        model.addAttribute("task", task);
        return "confirm-edit-task"; // 問題なければ確認画面へ
    }

    @PostMapping("/tasks/edit/complete")
    public String editComplete(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        taskService.save(task);
        redirectAttributes.addFlashAttribute("successMessage", "更新が完了しました！");
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/delete/confirm/{id}")
    public String deleteConfirm(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id);
        model.addAttribute("task", task);
        return "confirm-delete-task";
    }

    @PostMapping("/tasks/delete/complete")
    public String deleteComplete(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        Task taskToDelete = taskService.findById(task.getId());
        String title = taskToDelete.getTitle();

        taskService.deleteById(task.getId());

        redirectAttributes.addFlashAttribute("successMessage", "\"" + title + "\" を削除しました！");
        return "redirect:/tasks";
    }

    // ===== ここから追加：進捗の即時更新 =====
    @PostMapping("/tasks/{id}/progress")
    public String updateProgress(@PathVariable Long id,
                                 @RequestParam("status") String status,
                                 RedirectAttributes ra) {
        // 許容値バリデーション（ビューと揃える）
        List<String> allowed = List.of("未着手", "着手中", "完了");
        if (status == null || !allowed.contains(status)) {
            ra.addFlashAttribute("errorMessage", "不正な進捗値です。");
            return "redirect:/tasks";
        }

        try {
            Task task = taskService.findById(id);
            if (task == null) {
                throw new NoSuchElementException("task not found");
            }
            task.setStatus(status);
            taskService.save(task);

            ra.addFlashAttribute("successMessage", "進捗を更新しました。");
        } catch (NoSuchElementException e) {
            ra.addFlashAttribute("errorMessage", "対象のタスクが見つかりませんでした。");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "進捗の更新に失敗しました。");
        }
        return "redirect:/tasks";
    }
}
