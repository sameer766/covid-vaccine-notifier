package com.sameer.coviddatafetcher.repo;

import com.sameer.coviddatafetcher.model.Content;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepo extends JpaRepository<Content, Integer> {
  Optional<Content> findByTemplate(String template);
}
