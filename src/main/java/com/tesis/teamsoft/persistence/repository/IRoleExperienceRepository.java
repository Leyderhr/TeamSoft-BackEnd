package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.RoleExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleExperienceRepository extends JpaRepository<RoleExperienceEntity, Long> {

    Optional<RoleExperienceEntity> findByPerson_IdAndRole_Id(Long personId, Long roleId);

    void deleteByPerson_Id(Long personId);
}
