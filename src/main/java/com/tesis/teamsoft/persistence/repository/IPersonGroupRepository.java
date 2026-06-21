package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.PersonGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPersonGroupRepository extends JpaRepository<PersonGroupEntity, Long> {

    List<PersonGroupEntity> findAllByOrderByIdAsc();

    Optional<PersonGroupEntity> findByName(String name);

    @Query("SELECT COUNT(pg) > 0 FROM PersonGroupEntity pg WHERE pg.parentGroup.id = :parentId")
    boolean hasChildGroups(@Param("parentId") Long parentId);
}