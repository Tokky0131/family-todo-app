package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	boolean existsByTitleAndDescription(String title, String description);

	// 新規: 期限昇順で取得
	List<Task> findAllByOrderByDeadlineAsc();
}