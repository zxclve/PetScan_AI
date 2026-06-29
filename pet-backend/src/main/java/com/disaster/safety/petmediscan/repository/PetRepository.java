package com.disaster.safety.petmediscan.repository;

import com.disaster.safety.member.entity.Member;
import com.disaster.safety.petmediscan.entity.Pet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet,Long>{
    List<Pet> findByMember(Member member);
}
