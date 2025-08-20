package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // 読み取り
    @Transactional(readOnly = true)
    public List<Task> findAllOrderByDeadline() {
        return taskRepository.findAllByOrderByDeadlineAsc();
    }

    @Transactional(readOnly = true)
    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    // 作成・更新
    @Transactional
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    // 進捗だけ更新したい時に使えるヘルパー（任意）
    @Transactional
    public Task updateStatus(Long id, String status) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null) return null;
        task.setStatus(status);
        return taskRepository.save(task);
    }

    // 削除
    @Transactional
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    // 重複チェック
    @Transactional(readOnly = true)
    public boolean existsByTitleAndDescription(String title, String description) {
        return taskRepository.existsByTitleAndDescription(title, description);
    }
}
