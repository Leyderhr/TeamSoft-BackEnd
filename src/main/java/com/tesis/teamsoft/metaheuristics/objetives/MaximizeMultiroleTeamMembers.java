package com.tesis.teamsoft.metaheuristics.objetives;

import java.util.ArrayList;
import java.util.List;
import com.tesis.teamsoft.pojos.CompetencesTemplate;
import com.tesis.teamsoft.pojos.ProjectRoleCompetenceTemplate;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.persistence.entity.CompetenceValueEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.Getter;
import lombok.Setter;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import problem.definition.ObjetiveFunction;
import problem.definition.State;
import com.tesis.teamsoft.metaheuristics.auxiliary.ObjetiveFunctionUtil;
import problem.definition.Problem.ProblemType;

/**
 *
 * @author Derly
 */

@Getter
@Setter
public class MaximizeMultiroleTeamMembers extends ObjetiveFunction{

      private TeamFormationParameters parameters;

    public static String className = "Multirol";

    public MaximizeMultiroleTeamMembers(TeamFormationParameters parameters){
        super();
        this.parameters = parameters;
        setTypeProblem(ProblemType.Maximizar);
    }

       public MaximizeMultiroleTeamMembers() {
        super();
        setTypeProblem(ProblemType.Maximizar);
    }


    @Override
    public Double Evaluation(State state) {


        double roleCompetences = 0L;

        double maxCompetence = 0L;
        double minCompetence = 0L;
        double normalizeTotalProjectsCompetences;

        List<ProjectRoleCompetenceTemplate> requirements = parameters.getProjects(); //Lista de proyectos configurados por el usuario
        List<Object> projects = state.getCode();  //lista de proyectos - roles

        int p = 0;

        List<PersonEntity> allprojectWorkers;
        List<CompetenceValueEntity> personCompetences;
        List<CompetencesTemplate> competences;
        boolean roleFound;

        while(p < projects.size()){  // recorrer todos los proyectos
             ProjectRole projectRole = (ProjectRole) projects.get(p); // obtener proyecto actual

             ProjectRoleCompetenceTemplate projectRequirements = ProjectRoleCompetenceTemplate.findProjectById(requirements, projectRole.getProject()); //requerimentos del proyecto actual
             allprojectWorkers = ObjetiveFunctionUtil.ProjectWorkers(projectRole); //obtener todos los trabajadores del proyecto actual(p)

             int w = 0;

             while(w < allprojectWorkers.size()){ // recorrer todos los trabajadores del proyecto p

                 personCompetences = allprojectWorkers.get(w).getCompetenceValueList(); // obtener competencias de la persona

                 int r = 0;


                 while(r <  projectRole.getRoleWorkers().size()){ // recorrer todos los roles que requiere el proyecto p

                     competences = new ArrayList<>();

                     int n = 0; //
                     roleFound = false;


                     while(n < projectRequirements.getRoleCompetences().size() && !roleFound){ // recorrer todos los roles requeridos del proyecto para buscar sus competencias


                         if(projectRequirements.getRoleCompetences().get(n).getRole().getRoleName().equals(projectRole.getRoleWorkers().get(r).getRole().getRoleName())){ // si el rol requerido n coincide con el rol asignado r entonces llenar la lista de competencias requeridas del rol encontrado
                           competences.clear(); // My Changes
                           competences.addAll(projectRequirements.getRoleCompetences().get(n).getGenCompetences());
                           competences.addAll(projectRequirements.getRoleCompetences().get(n).getTechCompetences()); //concatenar competencias tecnicas y genericas

                           roleFound = true;

                           int c = 0;

                           while(c < competences.size()){ // recorrer las competencias del rol encontrado
                               roleCompetences += ObjetiveFunctionUtil.personCompetenceLevel(personCompetences, competences.get(c).getCompetence()) * competences.get(c).getCompetenceImportance().getLevels(); //nivel de la persona en la competencia actual
                               minCompetence += parameters.getMinLevel().getLevels() * competences.get(c).getCompetenceImportance().getLevels();
                               maxCompetence += parameters.getMaxLevel().getLevels() * competences.get(c).getCompetenceImportance().getLevels();
                               c++;
                           }
                         }
                          n++;
                     }

                     r++;
                 }

                 w++;
             }

          p++;
        }

        normalizeTotalProjectsCompetences = (roleCompetences - minCompetence)/(maxCompetence - minCompetence);
        return normalizeTotalProjectsCompetences;
    }

}
