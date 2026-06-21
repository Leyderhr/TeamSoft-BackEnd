package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.CompetenceValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICompetenceValueRepository extends JpaRepository<CompetenceValueEntity, Long> {

    Optional<CompetenceValueEntity> findByPerson_IdAndCompetence_Id(Long personId, Long competenceId);
}
