package org.example.repository;

import org.example.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findByIsPublicTrueOrderByUsageCountDesc();
    List<Template> findByCategoryAndIsPublicTrueOrderByUsageCountDesc(String category);
    List<Template> findByCreatorIdOrderByCreatedAtDesc(Long creatorId);
}