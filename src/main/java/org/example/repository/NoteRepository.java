package org.example.repository;

import org.example.entity.Note;
import org.example.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByProjectOrderByCreatedAtDesc(Project project);
    List<Note> findByProjectAndTypeOrderByCreatedAtDesc(Project project, String type);
}