package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.PersonTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPersonTestRepository extends JpaRepository<PersonTestEntity, Long> {

    Optional<PersonTestEntity> findByPerson_Id(Long personId);
}
