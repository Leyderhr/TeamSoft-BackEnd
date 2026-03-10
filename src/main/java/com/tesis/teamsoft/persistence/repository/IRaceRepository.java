package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.RaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRaceRepository extends JpaRepository<RaceEntity, Long> {

    List<RaceEntity> findAllByOrderByIdAsc();
}