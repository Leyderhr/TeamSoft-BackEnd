package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.ProjectEntity;
import com.tesis.teamsoft.persistence.entity.auxiliary.ProjectState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProjectRepository extends JpaRepository<ProjectEntity, Long> {
    List<ProjectEntity> findAllByOrderByIdAsc();
    boolean existsByProjectName(String projectName);
    List<ProjectEntity> findByState(ProjectState state);
}
