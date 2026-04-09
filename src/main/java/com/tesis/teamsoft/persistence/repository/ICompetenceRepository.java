package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.CompetenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICompetenceRepository extends JpaRepository<CompetenceEntity, Long> {

    List<CompetenceEntity> findAllByOrderByIdAsc();

    Optional<CompetenceEntity> findByCompetitionName(String competitionName);
}
