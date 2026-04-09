// com.tesis.teamsoft.metaheuristics.util.FactorEvaluation.java
package com.tesis.teamsoft.metaheuristics.auxiliary;

import com.tesis.teamsoft.persistence.entity.*;
import java.util.ArrayList;
import java.util.List;

public final class FactorEvaluation {

    // Constructor privado para evitar instanciación
    private FactorEvaluation() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final CategoryFactorEvaluator<NacionalityEntity> NACIONALITY =
            new CategoryFactorEvaluator<>(PersonEntity::getNacionality);

    public static final CategoryFactorEvaluator<AgeGroupEntity> AGE_GROUP =
            new CategoryFactorEvaluator<>(PersonEntity::getAgeGroup);

    public static final CategoryFactorEvaluator<ReligionEntity> RELIGION =
            new CategoryFactorEvaluator<>(PersonEntity::getReligion);

    public static double ageGroupEvaluation(List<Object> projects, List<PersonEntity> orgWorkers) {
        return AGE_GROUP.evaluateHeterogeneity(convert(projects), orgWorkers);
    }

    public static double ageGroupBalanceEvaluation(List<Object> projects, List<PersonEntity> orgWorkers) {
        return AGE_GROUP.evaluateBalance(convert(projects), orgWorkers);
    }

    public static double religionEvaluation(List<Object> projects, List<PersonEntity> orgWorkers) {
        return RELIGION.evaluateHeterogeneity(convert(projects), orgWorkers);
    }

    public static double religionBalanceEvaluation(List<Object> projects, List<PersonEntity> orgWorkers) {
        return RELIGION.evaluateBalance(convert(projects), orgWorkers);
    }

    private static List<ProjectRole> convert(List<Object> raw) {
        List<ProjectRole> result = new ArrayList<>(raw.size());
        for (Object obj : raw) result.add((ProjectRole) obj);
        return result;
    }
}