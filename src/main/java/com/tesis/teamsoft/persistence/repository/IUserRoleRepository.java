package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

    List<UserRoleEntity> findAllByOrderByIdAsc();
}
