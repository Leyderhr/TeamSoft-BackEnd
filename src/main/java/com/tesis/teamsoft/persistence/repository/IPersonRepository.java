package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.auxiliary.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IPersonRepository extends JpaRepository<PersonEntity, Long>, IPersonRepositoryCustom {
    List<PersonEntity> findAllByOrderByIdAsc();
    List<PersonEntity> findByGroup_IdInAndStatus(Set<Long> groupIds, Status status);

    // Importación CSV: existencia por nombre + experiencia + grupo (réplica de existePersona)
    Optional<PersonEntity> findFirstByPersonNameAndExperienceAndGroup_Id(String personName, Integer experience, Long groupId);
    boolean existsByEmail(String email);
    boolean existsByCard(String card);

    @Query("SELECT p FROM PersonEntity p WHERE p.ageGroup IS NULL AND p.birthDate >= :startDate AND p.birthDate <= :endDate")
    List<PersonEntity> findByAgeGroupIsNullAndBirthDateBetween(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT p FROM PersonEntity p WHERE p.ageGroup.id = :ageGroupId AND (p.birthDate <= :startDate OR p.birthDate >= :endDate)")
    List<PersonEntity> findByAgeGroupIdAndBirthDateNotBetween(
            @Param("ageGroupId") Long ageGroupId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}