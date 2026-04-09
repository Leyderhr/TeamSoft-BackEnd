package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.presentation.dto.FilterDTO;

import java.util.List;

public interface IPersonRepositoryCustom {
    List<PersonEntity> findByFilter(FilterDTO.FilterRequestDTO filter);
}