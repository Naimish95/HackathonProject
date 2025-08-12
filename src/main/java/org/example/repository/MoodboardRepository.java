package org.example.repository;

import org.example.entity.Moodboard;
import org.example.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MoodboardRepository extends JpaRepository<Moodboard, Long> {
    List<Moodboard> findByProjectOrderByCreatedAtDesc(Project project);
}