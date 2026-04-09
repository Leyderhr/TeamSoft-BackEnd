package com.tesis.teamsoft.metaheuristics.auxiliary.test;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BelbinCategoryRole {
    private List<PersonEntity> categoryWorkers;
}
