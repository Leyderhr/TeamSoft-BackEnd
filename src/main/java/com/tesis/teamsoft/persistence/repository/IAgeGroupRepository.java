package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.AgeGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAgeGroupRepository extends JpaRepository<AgeGroupEntity, Long> {

    List<AgeGroupEntity> findAllByOrderByIdAsc();
    boolean existsByAgeGroupName(String ageGroupName);
    boolean existsByAgeGroupNameAndIdNot(String ageGroupName, Long id);

    @Query("SELECT COUNT(ag) > 0 FROM AgeGroupEntity ag WHERE " +
            "(:minAge BETWEEN ag.minAge AND ag.maxAge OR " +
            ":maxAge BETWEEN ag.minAge AND ag.maxAge OR " +
            "(ag.minAge BETWEEN :minAge AND :maxAge AND ag.maxAge BETWEEN :minAge AND :maxAge)) " +
            "AND (:excludeId IS NULL OR ag.id != :excludeId)")
    boolean existsOverlappingAgeRange(@Param("minAge") int minAge,
                                      @Param("maxAge") int maxAge,
                                      @Param("excludeId") Long excludeId);
}
