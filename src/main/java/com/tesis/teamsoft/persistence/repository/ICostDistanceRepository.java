package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.CostDistanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICostDistanceRepository extends JpaRepository<CostDistanceEntity, Long> {

    List<CostDistanceEntity> findAllByOrderByIdAsc();

    @Query("SELECT COUNT(cd) > 0 FROM CostDistanceEntity cd WHERE " +
            "((cd.countyA.id = :countyAId AND cd.countyB.id = :countyBId) OR " +
            "(cd.countyA.id = :countyBId AND cd.countyB.id = :countyAId)) " +
            "AND (:excludeId IS NULL OR cd.id != :excludeId)")
    boolean existsByCountyPairExcludingId(@Param("countyAId") Long countyAId,
                                          @Param("countyBId") Long countyBId,
                                          @Param("excludeId") Long excludeId);

    CostDistanceEntity findFirstByOrderByCostDistanceDesc();
}