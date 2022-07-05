package com.sameer.emailservice.repository;

import com.sameer.emailservice.model.EmailObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepo extends JpaRepository<EmailObject, Integer> {
}
