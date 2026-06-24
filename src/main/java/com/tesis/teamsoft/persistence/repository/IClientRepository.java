package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IClientRepository extends JpaRepository<ClientEntity, Long> {

    List<ClientEntity> findAllByOrderByIdAsc();
    boolean existsByEntityName(String entityName);
    boolean existsByEntityNameAndIdNot(String entityName, Long id);
}