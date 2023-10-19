package br.com.dihnauer.todolist.tasks;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;

  private UUID userId;
  private String description;

  @Column(length = 50)
  private String title;

  @CreationTimestamp
  private LocalDateTime createdAt;

  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private String priority;

  public void setTitle(String title) throws Exception {
    if (title.length() > 50) {
      throw new Exception("Title must be less than 50 characters");
    }

    this.title = title;
  }

}
