package org.example.repository;

import org.example.entity.Project;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerOrderByCreatedAtDesc(User owner);
    List<Project> findByOwnerAndStatusOrderByCreatedAtDesc(User owner, String status);
    List<Project> findByOwnerAndCategoryOrderByCreatedAtDesc(User owner, String category);
}