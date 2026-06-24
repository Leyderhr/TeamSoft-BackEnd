package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.CountyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICountyRepository extends JpaRepository<CountyEntity, Long> {

    List<CountyEntity> findAllByOrderByIdAsc();
    boolean existsByCountyName(String countyName);
    boolean existsByCountyNameAndIdNot(String countyName, Long id);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
}