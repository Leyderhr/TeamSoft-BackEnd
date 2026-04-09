// com.tesis.teamsoft.metaheuristics.util.CategoryFactorEvaluator.java
package com.tesis.teamsoft.metaheuristics.auxiliary;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CategoryFactorEvaluator<T> {
    private final Function<PersonEntity, T> extractor;

    public CategoryFactorEvaluator(Function<PersonEntity, T> extractor) {
        this.extractor = extractor;
    }

    public int countDistinct(List<PersonEntity> persons) {
        List<T> distinct = new ArrayList<>();
        for (PersonEntity p : persons) {
            T value = extractor.apply(p);
            if (value != null && !distinct.contains(value)) {
                distinct.add(value);
            }
        }
        return distinct.size();
    }

    public double evaluateHeterogeneity(List<ProjectRole> projects, List<PersonEntity> organizationWorkers) {
        if (projects.isEmpty()) return 0.0;

        int totalDistinctOrg = countDistinct(organizationWorkers);
        double sum = 0.0;

        for (ProjectRole project : projects) {
            List<PersonEntity> team = ObjetiveFunctionUtil.ProjectWorkers(project);
            int teamDistinct = countDistinct(team);
            int maxDistinct = Math.min(team.size(), totalDistinctOrg);
            if (maxDistinct > 1)
                sum += (double) (teamDistinct - 1) / (maxDistinct - 1);
        }

        return sum / projects.size();
    }

    public double evaluateBalance(List<ProjectRole> projects, List<PersonEntity> organizationWorkers) {
        if (projects.isEmpty()) return 0.0;
        int totalDistinctOrg = countDistinct(organizationWorkers);
        if (totalDistinctOrg == 0) totalDistinctOrg = 1;

        List<Integer> teamCounts = new ArrayList<>();
        for (ProjectRole project : projects) {
            List<PersonEntity> team = ObjetiveFunctionUtil.ProjectWorkers(project);
            teamCounts.add(countDistinct(team));
        }

        double avg = teamCounts.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double sumAbsDiff = 0.0;
        for (int c : teamCounts) {
            sumAbsDiff += Math.abs(avg - c);
        }
        return sumAbsDiff / (projects.size() * totalDistinctOrg);
    }
}