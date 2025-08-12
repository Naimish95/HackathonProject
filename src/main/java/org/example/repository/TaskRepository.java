package org.example.repository;

import org.example.entity.Task;
import org.example.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectOrderByCreatedAtDesc(Project project);
    List<Task> findByProjectAndStatusOrderByCreatedAtDesc(Project project, String status);
    
    @Query("SELECT t FROM Task t WHERE t.project.owner.id = ?1 AND t.dueDate BETWEEN ?2 AND ?3")
    List<Task> findUpcomingTasksByUser(Long userId, LocalDateTime start, LocalDateTime end);
}