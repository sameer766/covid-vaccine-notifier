package com.sameer.coviddatafetcher.repo;

import com.sameer.coviddatafetcher.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByUserEmail(String email);
}
