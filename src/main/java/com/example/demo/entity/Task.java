package com.example.demo.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "task")
@EntityListeners(AuditingEntityListener.class) // ★ 監査リスナー有効化
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "タイトルは必須です。")
    @Size(max = 10, message = "タイトルは10文字以内で入力してください。")
    @Column(nullable = false, length = 10)
    private String title;

    @Size(max = 20, message = "詳細は20文字以内で入力してください。")
    @Column(length = 20)
    private String description;

    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    // ★ 追加：作成日時・更新日時（監査）
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // ★ 追加：期限の許容範囲（1900-01-01〜2100-12-31）
    @AssertTrue(message = "期限は 1900-01-01 〜 2100-12-31 の範囲で入力してください。")
    public boolean isDeadlineInAllowedRange() {
        if (deadline == null) return true; // 未入力は許容（現行仕様踏襲）
        return !deadline.isBefore(LocalDate.of(1900, 1, 1))
            && !deadline.isAfter(LocalDate.of(2100, 12, 31));
    }
}
