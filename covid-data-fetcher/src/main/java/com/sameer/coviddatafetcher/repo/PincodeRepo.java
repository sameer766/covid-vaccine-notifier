package com.sameer.coviddatafetcher.repo;

import com.sameer.coviddatafetcher.entity.PincodeDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PincodeRepo extends JpaRepository<PincodeDetails, Integer> {
   Optional<PincodeDetails> findByPincode(String pinCode);
}
