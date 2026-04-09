package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.PersonalProjectInterestsEntity;
import com.tesis.teamsoft.persistence.entity.ProjectEntity;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

public class BossTeamInterest extends Constrain {

    @Override
    public Boolean ValidateState(State state) {

        List<Object> projects = state.getCode(); //lista de proyectos

        int i = 0;
        boolean meet = true;
        while (i < projects.size() && meet) { //para cada proyecto -rol

            ProjectRole projectRole = (ProjectRole) projects.get(i);
            ProjectEntity project = projectRole.getProject();

            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();

            int j = 0;
            while (j < roleWorkers.size() && meet) { //para cada rol trabajador
                RoleWorker roleWorker = roleWorkers.get(j);

                if (roleWorker.getRole().isBoss()) { // si el rol actual es jefe de proyecto
                    List<PersonEntity> aux = new ArrayList<>();
                    aux.addAll(roleWorker.getWorkers());
                    aux.addAll(roleWorker.getFixedWorkers());
                    int k = 0;
                    while (k < aux.size() && meet) { //para cada persona

                        PersonEntity worker = aux.get(k);
                        meet = validatePerson(worker, project);
                        k++;
                    }
                }
                j++;
            }
            i++;
        }
        return meet;
    }

    public boolean validatePerson(PersonEntity worker, ProjectEntity project) {
        boolean meet = true;

        List<PersonalProjectInterestsEntity> personalProjectInterestses = worker.getPersonalProjectInterestsList();
        int i = 0;
        while (i < personalProjectInterestses.size() && meet) {
            PersonalProjectInterestsEntity personalProjectInterests = personalProjectInterestses.get(i);
            if (personalProjectInterests.getProject().getId().equals(project.getId())) { // si es el equipo
                if (personalProjectInterests.isPreference()) { // si esta interesado
                    return true;
                } else {
                    meet = false;
                }
            }
            i++;
        }
        return meet;
    }
}
