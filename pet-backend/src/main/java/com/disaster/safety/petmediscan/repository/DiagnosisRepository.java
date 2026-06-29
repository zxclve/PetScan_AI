package com.disaster.safety.petmediscan.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.disaster.safety.petmediscan.entity.Diagnosis;
import com.disaster.safety.petmediscan.entity.Pet;

public interface DiagnosisRepository extends JpaRepository<Diagnosis,Long> {
    List<Diagnosis> findByPet(Pet pet);
}
