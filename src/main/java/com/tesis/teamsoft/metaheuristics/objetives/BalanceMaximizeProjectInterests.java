package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.PersonalProjectInterestsEntity;
import com.tesis.teamsoft.persistence.entity.ProjectEntity;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lesmes
 */
public class BalanceMaximizeProjectInterests extends ObjetiveFunction {

    public static String className = "BalanceProjectInterests";

    @Override
    public Double Evaluation(State state) {
        ArrayList<PersonEntity> projectWorkerList = new ArrayList<>();
        List<Double> reasons = new ArrayList<>();
        List<Object> projects = state.getCode();
        double projectInterest;
        double normalization;
        double projectPersons;
        double projectReason;
        double reasonSum = 0;
        double finalReasonSum = 0;
        double reasonAvg;
        double max;
        int teamsAmount = projects.size();

        for (Object item : projects) {
            projectPersons = 0;
            projectInterest = 0;
            ProjectRole projectRole = (ProjectRole) item;
            ProjectEntity project = projectRole.getProject();
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            for (RoleWorker roleWorker : roleWorkers) {
                List<PersonEntity> workers = roleWorker.getWorkers();
                for (PersonEntity worker : workers) {
                    if (addWorker(projectWorkerList, worker)) {
                        projectPersons += 1;
                        List<PersonalProjectInterestsEntity> personalProjectInterests = worker.getPersonalProjectInterestsList();
                        for (PersonalProjectInterestsEntity ppi : personalProjectInterests) {
                            //if (ppi.getProjectFk().getProjectName().equalsIgnoreCase(project.getProjectName())) {
                            if (ppi.getProject().getId().equals(project.getId())) {
                                if (ppi.isPreference()) {
                                    projectInterest += 1;
                                }
                            }
                        }
                    }
                }
            }
            projectReason = projectInterest / projectPersons;
            reasons.add(projectReason);
            projectWorkerList.clear();
        }
        for (Double aDouble : reasons) {
            reasonSum += aDouble;
        }
        reasonAvg = reasonSum / teamsAmount;
        for (Double reason : reasons) {
            finalReasonSum += Math.abs(reason - reasonAvg);
        }
        max = getMax(teamsAmount);

        normalization = (finalReasonSum - 0) / (max - 0);

        return normalization;
    }

    private boolean addWorker(List<PersonEntity> workersList, PersonEntity worker) {
        boolean added = false;
        boolean found = false;
        for (PersonEntity w : workersList) {
            if (w.getId().equals(worker.getId())) {
                found = true;
                break;
            }
        }
        if (!found) {
            workersList.add(worker);
            added = true;
        }
        return added;
    }

    private double getMax(int size) { //2
        double average;
        double max = 0;
        int teamsAmountWithFullPreference;
        int teamsAmountWithVoidPreference;

        if (size % 2 == 0) {
            teamsAmountWithFullPreference = size / 2;  //1
            teamsAmountWithVoidPreference = size / 2; //1
        } else {
            teamsAmountWithFullPreference = (size + 1) / 2;
            teamsAmountWithVoidPreference = (size - 1) / 2;
        }
        average = (double) teamsAmountWithFullPreference / size; //0.5

        for (int i = 0; i < teamsAmountWithFullPreference; i++) {
            max += Math.abs(1 - average);                       // 0.5
        }
        for (int j = 0; j < teamsAmountWithVoidPreference; j++) {
            max += Math.abs(0 - average);   //0.5
        }

        return max; //1
    }

}
