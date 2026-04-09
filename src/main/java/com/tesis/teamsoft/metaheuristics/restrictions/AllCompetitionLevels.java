package com.tesis.teamsoft.metaheuristics.restrictions;

import com.tesis.teamsoft.pojos.CompetencesTemplate;
import com.tesis.teamsoft.pojos.ProjectRoleCompetenceTemplate;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.CompetenceValueEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Que la persona tenga el nivel minimo o superior en todas las competencias tec
 * y gen
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllCompetitionLevels extends Constrain {

    private TeamFormationParameters parameters;

    @Override
    public Boolean ValidateState(State state) {

        List<ProjectRoleCompetenceTemplate> requirements = this.parameters.getProjects(); //Lista de proyectos configurados por el usuario
        List<Object> projects = state.getCode();  //lista de proyectos - roles

        int i = 0;
        boolean meet = true;
        while (i < projects.size() && meet) {  // para cada proyecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);

            ProjectRoleCompetenceTemplate projectRequirements = ProjectRoleCompetenceTemplate.findProjectById(requirements, projectRole.getProject()); //requerimentos del proyecto actual

            int j = 0;
            while (j < projectRole.getRoleWorkers().size() && meet) { //para cada rol-persona
                RoleWorker rolePerson = projectRole.getRoleWorkers().get(j);
                List<PersonEntity> aux = new ArrayList<>(); // concatenar listas de personas y personas fijadas por el usuario
                aux.addAll(rolePerson.getWorkers());
                aux.addAll(rolePerson.getFixedWorkers());

                int k = 0;
                while (k < aux.size() && meet) { // para cada persona
                    PersonEntity worker = aux.get(k);
                    meet = validatePerson(projectRequirements, rolePerson, worker);
                    k++;
                }
                j++;
            }
            i++;
        }
        return meet;
    }


    public boolean validatePerson(ProjectRoleCompetenceTemplate projectRequirements, RoleWorker rolePerson, PersonEntity worker) {

        //projectRequirements -> para saber los valores minimo y maximo exigido en el proyecto
        //rolePerson-> el rol al que se va a analizar si la persona pertenece a el y tiene las competencias necesarias

        boolean meet = true;

        List<CompetenceValueEntity> personCompetences = worker.getCompetenceValueList(); // obtener competencias de la persona
        int l = 0;
        boolean found = false;
        while (l < projectRequirements.getRoleCompetences().size() && !found) { // para cada rol requerido
            if (rolePerson.getRole().getRoleName().equalsIgnoreCase(projectRequirements.getRoleCompetences().get(l).getRole().getRoleName())) { //si la persona juega el rol

                List<CompetencesTemplate> competences = new ArrayList<>(); //concatenar competencias técnicas y genéricas
                competences.addAll(projectRequirements.getRoleCompetences().get(l).getGenCompetences());
                competences.addAll(projectRequirements.getRoleCompetences().get(l).getTechCompetences());

                found = true;

                int m = 0;
                while (m < competences.size() && meet) { //para cada competencia

                    if (!ObjetiveFunctionUtil.validatePersonCompetences(personCompetences, competences.get(m).getCompetence(), competences.get(m).getMinLevel().getLevels())) { //Chequear q posee el nivel mínimo
                        meet = false;
                    }
                    m++;
                }
            }
            l++;
        }
        return meet;
    }
}
