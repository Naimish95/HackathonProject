package org.example.repository;

import org.example.entity.UserStory;
import org.example.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, Long> {
    List<UserStory> findByProjectOrderByCreatedAtDesc(Project project);
    List<UserStory> findByProjectAndStatusOrderByCreatedAtDesc(Project project, String status);
}