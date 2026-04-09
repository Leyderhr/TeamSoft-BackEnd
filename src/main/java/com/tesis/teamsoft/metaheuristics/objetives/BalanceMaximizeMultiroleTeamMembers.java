package com.tesis.teamsoft.metaheuristics.objetives;

import java.util.ArrayList;
import java.util.List;
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
import problem.definition.ObjetiveFunction;
import problem.definition.State;

/**
 *
 * @author Rym
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceMaximizeMultiroleTeamMembers extends ObjetiveFunction {

    private TeamFormationParameters parameters;

    public static String className = "BalanceMultirol";

    @Override
    public Double Evaluation(State state) {

        double roleCompetences;

        double maxCompetence;
        double minCompetence;
        double projectCompetence;
        double sumCompetenceMax;
        double maxSumProject;
        int amount_projects_max;
        int p = 0;
        List<Double> projectSumMax = new ArrayList<>();
        List<ProjectRoleCompetenceTemplate> requirements = parameters.getProjects(); //Lista de proyectos configurados por el usuario
        List<Object> projects = state.getCode();  //lista de proyectos - roles

        List<PersonEntity> allprojectWorkers;
        List<CompetenceValueEntity> personCompetences;
        List<CompetencesTemplate> competences;
        boolean roleFound;
        List<Double> forBalance = new ArrayList<>();
        double balancedValue;
        double minSumProject;

        if (projects.size() % 2 == 0) { //Si la cantidad de proyectos es par
            amount_projects_max = projects.size() / 2;
        } else {
            amount_projects_max = (projects.size() + 1) / 2;
        }

        while (p < projects.size()) { // recorrer todos los proyectos 
            System.out.println("Proyecto : " + p);
            ProjectRole projectRole = (ProjectRole) projects.get(p); // obtener proyecto actual
            ProjectRoleCompetenceTemplate projectRequirements = ProjectRoleCompetenceTemplate.
                    findProjectById(requirements, projectRole.getProject()); //requerimentos del proyecto actual
            allprojectWorkers = ObjetiveFunctionUtil.ProjectWorkers(projectRole); //obtener todos los trabajadores del proyecto actual(p)
            maxSumProject = 0;
            minSumProject = 0;
            projectCompetence = 0;
            int w = 0;
            while (w < allprojectWorkers.size()) { // recorrer todos los trabajadores del proyecto p
                System.out.println("Persona: " + allprojectWorkers.get(w).getPersonName());
                personCompetences = allprojectWorkers.get(w).getCompetenceValueList();// obtener competencias de la persona
                roleCompetences = 0;
                minCompetence = 0;
                maxCompetence = 0;
                int r = 0;
                while (r < projectRole.getRoleWorkers().size()) { // recorrer todos los roles que requiere el proyecto p
                    System.out.println("Rol: " + projectRole.getRoles().get(r).getRoleName());
                    competences = new ArrayList<>();

                    int n = 0; //
                    roleFound = false;

                    while (n < projectRequirements.getRoleCompetences().size() && !roleFound) { // recorrer todos los roles requeridos del proyecto para buscar sus competencias
                        roleCompetences = 0;
                        minCompetence = 0;
                        maxCompetence = 0;
                        /* si el rol requerido n coincide con el rol asignado r entonces llenar
                         la lista de competencias requeridas del rol encontrado*/
                        if (projectRequirements.getRoleCompetences().get(n).getRole().getRoleName().
                                equals(projectRole.getRoleWorkers().get(r).getRole().getRoleName())) {
                            competences.clear(); // My Changes
                            competences.addAll(projectRequirements.getRoleCompetences().get(n).getGenCompetences());
                            competences.addAll(projectRequirements.getRoleCompetences().get(n).getTechCompetences()); //concatenar competencias tecnicas y genericas

                            roleFound = true;

                            int c = 0;

                            while (c < competences.size()) { // recorrer las competencias del rol encontrado
                                roleCompetences += ObjetiveFunctionUtil.personCompetenceLevel(personCompetences,
                                        competences.get(c).getCompetence()) * competences.get(c).getCompetenceImportance()
                                        .getLevels(); //nivel de la persona en la competencia actual
                                System.out.println("Competencia: " + competences.get(c).getCompetence().getCompetitionName());
                                System.out.println("Competencia del rol: " + ObjetiveFunctionUtil.personCompetenceLevel(personCompetences,
                                        competences.get(c).getCompetence()) * competences.get(c).getCompetenceImportance()
                                        .getLevels());
                                minCompetence += parameters.getMinLevel().getLevels() * competences.get(c).
                                        getCompetenceImportance().getLevels();
                                maxCompetence += parameters.getMaxLevel().getLevels() * competences.get(c).
                                        getCompetenceImportance().getLevels();
                                c++;
                            }
                        }
                        n++;
                    }
                    projectCompetence += roleCompetences;
                    System.out.println("ProjectCompetence> " + projectCompetence);
                    maxSumProject += maxCompetence;
                    System.out.println("maxSumProject: " + maxSumProject);
                    minSumProject += minCompetence;
                    System.out.println("minSumProject: " + minSumProject);
                    r++;
                }

                w++;
            }
            forBalance.add(projectCompetence);

            if (amount_projects_max > 0) {
                projectSumMax.add(maxSumProject); //
                amount_projects_max--;
            } else {
                projectSumMax.add(minSumProject);

            }
            p++;
        }

        balancedValue = ObjetiveFunctionUtil.balance(forBalance); //balancear funcion objetivo
        for (Double item : forBalance) {
            System.out.println("Lista para balancear: " + item);
        }

        System.out.println("Balance Value: " + balancedValue);

        for (Double item : projectSumMax) {
            System.out.println("Lista para Max Valor de Balance: " + item);
        }
        //Calcular el maximo valor de balance
        sumCompetenceMax = ObjetiveFunctionUtil.balance(projectSumMax);
        System.out.println("Max valor de balance: " + sumCompetenceMax);
        balancedValue = balancedValue / sumCompetenceMax;
        System.out.println("BalanceFinal: " + balancedValue);
        return balancedValue;
    }
}
