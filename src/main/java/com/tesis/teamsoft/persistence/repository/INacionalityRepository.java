package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.NacionalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface INacionalityRepository extends JpaRepository<NacionalityEntity, Long> {

    List<NacionalityEntity> findAllByOrderByIdAsc();
    boolean existsByPaisNac(String paisNac);
    boolean existsByPaisNacAndIdNot(String paisNac, Long id);
    boolean existsByGentilicioNac(String gentilicioNac);
    boolean existsByGentilicioNacAndIdNot(String gentilicioNac, Long id);
}