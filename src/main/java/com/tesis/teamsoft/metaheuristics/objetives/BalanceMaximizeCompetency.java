package com.tesis.teamsoft.metaheuristics.objetives;

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
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;


/**
 * @author G1lb3rt
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceMaximizeCompetency
        extends ObjetiveFunction {
    public static String className = "BalanceCompetencias";

    private TeamFormationParameters parameters;

    @Override
    public Double Evaluation(State state) {

        double competenceLevel;
        double importance;
        double sumCompetenceMax;
        double projectCompetence;
        List<Double> projectSumMax = new ArrayList<>();

        List<Object> projects = state.getCode();
        int amount_projects_max;
        if (projects.size() % 2 == 0) { //Si la cantidad de proyectos es par
            amount_projects_max = projects.size() / 2;
        } else {
            amount_projects_max = (projects.size() + 1) / 2;
        }
        List<ProjectRoleCompetenceTemplate> requirements = parameters.getProjects(); //Lista de proyectos configurados por el usuario

        List<Double> forBalance = new ArrayList<>();
        double balancedValue;

        int i = 0;
        double maxSumProject;
        PersonEntity worker;
        List<CompetenceValueEntity> personCompetences;
        List<CompetencesTemplate> competences;
        int l;
        boolean found;
        while (i < projects.size()) {  // para cada proyecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            ProjectRoleCompetenceTemplate projectRequirements = ProjectRoleCompetenceTemplate.findProjectById(requirements, projectRole.getProject()); //requerimentos del proyecto actual
            projectCompetence = 0;
            maxSumProject = 0;

            int j = 0;
            while (j < projectRole.getRoleWorkers().size()) { //para cada rol-persona
                RoleWorker rolePerson = projectRole.getRoleWorkers().get(j);

                int k = 0;
                while (k < rolePerson.getWorkers().size()) { // para cada persona
                    worker = rolePerson.getWorkers().get(k);
                    personCompetences = worker.getCompetenceValueList(); // obtener competencias de la persona

                    competences = new ArrayList<>();
                    l = 0;
                    found = false;

                    while (l < projectRequirements.getRoleCompetences().size() && !found) { // para cada rol requerido
                        if (rolePerson.getRole().getRoleName().equalsIgnoreCase(projectRequirements.getRoleCompetences().get(l).getRole().getRoleName())) { //si la persona juega el rol
                            competenceLevel = 0L;
                            importance = 0L;
                            sumCompetenceMax = 0;

                            competences.clear(); // My Changes
                            competences.addAll(projectRequirements.getRoleCompetences().get(l).getGenCompetences());
                            competences.addAll(projectRequirements.getRoleCompetences().get(l).getTechCompetences()); //concatenar competencias tecnicas y genericas
                            found = true;

                            int m = 0;
                            while (m < competences.size()) { //para cada competencia
                                competenceLevel += ObjetiveFunctionUtil.personCompetenceLevel(personCompetences, competences.get(m).getCompetence()) * competences.get(m).getCompetenceImportance().getLevels(); //nivel de la persona en la competencia actual
                                sumCompetenceMax += parameters.getMaxLevel().getLevels() * competences.get(m).getCompetenceImportance().getLevels();
                                importance += competences.get(m).getCompetenceImportance().getLevels();

                                m++;
                            }
                            projectCompetence = projectCompetence + competenceLevel / importance;
                            maxSumProject += sumCompetenceMax / importance;
                        }
                        l++;
                    }
                    k++;
                }
                k = 0;
                while (k < rolePerson.getFixedWorkers().size()) { // para cada persona
                    worker = rolePerson.getFixedWorkers().get(k);
                    personCompetences = worker.getCompetenceValueList(); // obtener competencias de la persona

                    competences = new ArrayList<>();
                    l = 0;
                    found = false;

                    while (l < projectRequirements.getRoleCompetences().size() && !found) { // para cada rol requerido
                        if (rolePerson.getRole().getRoleName().equalsIgnoreCase(projectRequirements.getRoleCompetences().get(l).getRole().getRoleName())) { //si la persona juega el rol
                            competenceLevel = 0L;
                            importance = 0L;
                            sumCompetenceMax = 0;

                            competences.addAll(projectRequirements.getRoleCompetences().get(l).getGenCompetences());
                            competences.addAll(projectRequirements.getRoleCompetences().get(l).getTechCompetences()); //concatenar competencias tecnicas y genericas
                            found = true;

                            int m = 0;
                            while (m < competences.size()) { //para cada competencia
                                competenceLevel += ObjetiveFunctionUtil.personCompetenceLevel(personCompetences, competences.get(m).getCompetence()) * competences.get(m).getCompetenceImportance().getLevels(); //nivel de la persona en la competencia actual
                                sumCompetenceMax += parameters.getMaxLevel().getLevels() * competences.get(m).getCompetenceImportance().getLevels();
                                importance += competences.get(m).getCompetenceImportance().getLevels();

                                m++;
                            }
                            projectCompetence = projectCompetence + competenceLevel / importance;
                            maxSumProject += sumCompetenceMax / importance;
                        }
                        l++;
                    }
                    k++;
                }
                j++;
            }
            forBalance.add(projectCompetence / maxSumProject);


            if (amount_projects_max > 0) {
                projectSumMax.add(1d); //La razón de máximo interes es 1, indicando que todos tienen preferencia por los roles asignados
                amount_projects_max--;
            } else {
                projectSumMax.add(0d); //La razón de mínimo interés es 0, indicando que nadie tiene preferencia por los roles asignados
            }

            i++;
        }
        balancedValue = ObjetiveFunctionUtil.balance(forBalance); //balancear funcion objetivo

        //Calcular el máximo valor de balance
        sumCompetenceMax = ObjetiveFunctionUtil.balance(projectSumMax);

        balancedValue = balancedValue / sumCompetenceMax;
        return balancedValue;


    }


}
