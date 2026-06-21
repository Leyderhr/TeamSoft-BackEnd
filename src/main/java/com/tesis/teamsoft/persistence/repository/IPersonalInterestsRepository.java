package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.PersonalInterestsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPersonalInterestsRepository extends JpaRepository<PersonalInterestsEntity, Long> {

    Optional<PersonalInterestsEntity> findByPerson_IdAndRole_Id(Long personId, Long roleId);

    void deleteByPerson_Id(Long personId);
}
