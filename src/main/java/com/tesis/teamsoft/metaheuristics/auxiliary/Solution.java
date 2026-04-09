package com.tesis.teamsoft.metaheuristics.auxiliary;

import lombok.Data;

import java.util.List;

@Data
public class Solution {

    private List<ProjectRole> solutions;

    private float maxComp;
    private float minCost;
    private float minIncomp;
    private float minWorkLoad;
    private float maxBelbinRoles; //caracteristicas psicologicas
    private float maxInterests;
    private float cost;// si se usa factores ponderados
}
