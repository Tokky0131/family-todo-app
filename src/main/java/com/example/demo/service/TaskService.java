package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // 期限順で取得するメソッド
    public List<Task> findAllOrderByDeadline() {
        return taskRepository.findAllByOrderByDeadlineAsc();
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }
    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }
    public boolean existsByTitleAndDescription(String title, String description) {
        return taskRepository.existsByTitleAndDescription(title, description);
    }
}