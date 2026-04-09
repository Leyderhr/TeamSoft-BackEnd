package com.tesis.teamsoft.metaheuristics.auxiliary.test;

import com.tesis.teamsoft.persistence.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompetenceRoleWorker {

    private RoleEntity role;
    private List<CompetentWorker> workers;
}
