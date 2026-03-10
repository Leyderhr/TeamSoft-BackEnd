package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.ProjectStructureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProjectStructureRepository extends JpaRepository<ProjectStructureEntity, Long> {
    List<ProjectStructureEntity> findAllByOrderByIdAsc();
}