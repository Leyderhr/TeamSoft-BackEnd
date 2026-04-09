package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;;

@Repository
public interface IRoleRepository extends JpaRepository<RoleEntity, Long> {

    List<RoleEntity> findAllByOrderByIdAsc();

    Optional<RoleEntity> findByRoleName(String roleName);
}