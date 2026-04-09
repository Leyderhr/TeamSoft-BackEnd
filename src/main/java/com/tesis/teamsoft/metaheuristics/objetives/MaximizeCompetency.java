package com.tesis.teamsoft.metaheuristics.objetives;

import com.tesis.teamsoft.pojos.CompetencesTemplate;
import com.tesis.teamsoft.pojos.ProjectRoleCompetenceTemplate;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.CompetenceValueEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.Getter;
import lombok.Setter;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MaximizeCompetency
        extends ObjetiveFunction {

    private TeamFormationParameters parameters;

    public static String className = "Competencias";


    public MaximizeCompetency(TeamFormationParameters parameters) {
        super();
        this.parameters = parameters;
        setTypeProblem(ProblemType.Maximizar);
    }

    public MaximizeCompetency() {
        super();
        setTypeProblem(ProblemType.Maximizar);
    }

    @Override
    public Double Evaluation(State state) {

        double maxCompetence = 0;

        double competenceLevel;
        double importance;
        double sumMaxCompetence;

        double sumCompetenceMax = 0L;
        double sumCompetenceMin = 0L;

        List<ProjectRoleCompetenceTemplate> requirements = parameters.getProjects(); //Lista de proyectos configurados por el usuario
        List<Object> projects = state.getCode();  //lista de proyectos - roles

        int i = 0;
        PersonEntity worker;
        List<CompetenceValueEntity> personCompetences;
        List<CompetencesTemplate> competences;
        int l;
        boolean found;
        while (i < projects.size()) {  // para cada proyecto-rol
            ProjectRole projectRole = (ProjectRole) projects.get(i);
            ProjectRoleCompetenceTemplate projectRequirements = ProjectRoleCompetenceTemplate.findProjectById(requirements, projectRole.getProject()); //requerimentos del proyecto actual

            int j = 0;

            while (j < projectRole.getRoleWorkers().size()) { //para cada rol-persona
                RoleWorker rolePerson = projectRole.getRoleWorkers().get(j);

                int k = 0;
                while (k < rolePerson.getWorkers().size()) { // para cada persona
                    worker = rolePerson.getWorkers().get(k);
                    personCompetences = worker.getCompetenceValueList(); // obtener competencias de la persona
                    //List<CompetencesTemplate> competences;
                    competences = new ArrayList<>();
                    l = 0;
                    found = false;

                    while (l < projectRequirements.getRoleCompetences().size() && !found) { // para cada rol requerido
                        if (rolePerson.getRole().getRoleName().equalsIgnoreCase(projectRequirements.getRoleCompetences().get(l).getRole().getRoleName())) { //si la persona juega el rol
                            competenceLevel = 0L;
                            importance = 0L;
                            sumMaxCompetence = 0L;

                            //sumCompetenceMax += (rolePerson.getWorkers().size()) * (projectRequirements.getRoleCompetences().get(l).getGenCompetences().size() + projectRequirements.getRoleCompetences().get(l).getTechCompetences().size()) * parameters.getMaxLevel().getLevels();

                            // My Changes
                            competences.addAll(projectRequirements.getRoleCompetences().get(l).getGenCompetences());
                            competences.addAll(projectRequirements.getRoleCompetences().get(l).getTechCompetences()); //concatenar competencias tecnicas y genericas
                            found = true;

                            int m = 0;
                            while (m < competences.size()) { //para cada competencia
                                competenceLevel += ObjetiveFunctionUtil.personCompetenceLevel(personCompetences, competences.get(m).getCompetence()) * competences.get(m).getCompetenceImportance().getLevels(); //nivel de la persona en la competencia actual
                                sumMaxCompetence += parameters.getMaxLevel().getLevels() * competences.get(m).getCompetenceImportance().getLevels();
                                importance += competences.get(m).getCompetenceImportance().getLevels();

                                m++;
                            }
                            if (importance != 0){ // si no hay competencia asociadas a al rol NaN dividas
                                maxCompetence += competenceLevel / importance;
                                sumCompetenceMax += sumMaxCompetence / importance;
                            }else {
                                break; // mejor sal del ciclo :)
                            }
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
                            sumMaxCompetence = 0L;

                            // My Changes
                            competences.addAll(projectRequirements.getRoleCompetences().get(l).getGenCompetences());
                            competences.addAll(projectRequirements.getRoleCompetences().get(l).getTechCompetences()); //concatenar competencias tecnicas y genericas
                            found = true;

                            int m = 0;
                            while (m < competences.size()) { //para cada competencia
                                competenceLevel += ObjetiveFunctionUtil.personCompetenceLevel(personCompetences, competences.get(m).getCompetence()) * competences.get(m).getCompetenceImportance().getLevels(); //nivel de la persona en la competencia actual
                                sumMaxCompetence += parameters.getMaxLevel().getLevels() * competences.get(m).getCompetenceImportance().getLevels();
                                importance += competences.get(m).getCompetenceImportance().getLevels();

                                m++;
                            }
                            maxCompetence += competenceLevel / importance;
                            sumCompetenceMax += sumMaxCompetence / importance;
                        }
                        l++;
                    }
                    k++;
                }
                j++;
            }
            i++;
        }
        maxCompetence = (maxCompetence - sumCompetenceMin) / (sumCompetenceMax - sumCompetenceMin);
        return maxCompetence;
    }

}
