package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.auxiliary.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IPersonRepository extends JpaRepository<PersonEntity, Long>, IPersonRepositoryCustom {
    List<PersonEntity> findAllByOrderByIdAsc();
    List<PersonEntity> findByGroup_IdInAndStatus(Set<Long> groupIds, Status status);

}