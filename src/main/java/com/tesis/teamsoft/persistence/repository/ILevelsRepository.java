package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.LevelsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILevelsRepository extends JpaRepository<LevelsEntity, Long> {

    List<LevelsEntity> findAllByOrderByIdAsc();

    List<LevelsEntity> findAllByOrderByLevelsAsc();

    LevelsEntity findFirstByOrderByLevelsDesc();

    LevelsEntity findFirstByOrderByLevelsAsc();

    boolean existsByLevels(long levels);
    boolean existsByLevelsAndIdNot(long levels, Long id);
    boolean existsBySignificance(String significance);
    boolean existsBySignificanceAndIdNot(String significance, Long id);
}