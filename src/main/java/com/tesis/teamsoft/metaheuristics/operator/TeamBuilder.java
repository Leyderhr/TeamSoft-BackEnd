package com.tesis.teamsoft.metaheuristics.operator;

import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.entity.auxiliary.Status;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import com.tesis.teamsoft.pojos.FixedWorker;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRole;
import com.tesis.teamsoft.metaheuristics.auxiliary.ProjectRoleState;
import com.tesis.teamsoft.metaheuristics.auxiliary.RoleWorker;

import java.util.ArrayList;
import java.util.List;

public class TeamBuilder {

    /**
     * Para construir una solucion inicial vacia (contendra solo las personas
     * que ya fueron asignadas al proyecto con anterioridad)
     *
     */
    public static ProjectRoleState getInitialVoidSolution(TeamFormationParameters parameters) {

        ProjectRoleState initialSolution = new ProjectRoleState(); //estado inicial

        ArrayList<ProjectRole> projectRoles = new ArrayList<>(); //codificacion del problema
        ProjectRole projectRole;
        List<RoleWorker> roleWorkers; // roles-personas  para la codificación
        RoleWorker roleWorker;
        ProjectEntity project;
        int i = 0;
        boolean found = false;

        if (parameters != null) {
            if (parameters.getProjects() != null) {
                while (i < parameters.getProjects().size() && !found) { //para cada proyecto
                    project = new ProjectEntity(); //para evitar actualizar referencias
                    project.setClient(parameters.getProjects().get(i).getProject().getClient());
                    project.setClose(parameters.getProjects().get(i).getProject().isClose());
                    project.setCycleList(parameters.getProjects().get(i).getProject().getCycleList());
                    project.setEndDate(parameters.getProjects().get(i).getProject().getEndDate());
                    project.setFinalize(parameters.getProjects().get(i).getProject().isFinalize());
                    project.setId(parameters.getProjects().get(i).getProject().getId());
                    project.setInitialDate(parameters.getProjects().get(i).getProject().getInitialDate());
                    project.setProjectName(parameters.getProjects().get(i).getProject().getProjectName());
                    project.setProvince(parameters.getProjects().get(i).getProject().getProvince());

                    projectRole = new ProjectRole();
                    projectRole.setProject(project); //fijar proyecto en la codificación

                    CycleEntity lastCycle = lastProjectCycle(project); // obtener último ciclo del proyecto (basado en que este no tendrá fecha de fin)
                    ProjectStructureEntity structure = lastCycle.getProjectStructure(); //del  ultimo ciclo del proyecto obtener su estructura
                    List<ProjectRolesEntity> neededRoles = structure.getProjectRolesList(); // de la estructura obtener roles necesarios para desarrollar el proyecto

                    roleWorkers = new ArrayList<>(); //crear nueva lista de roles-personas para cada proyecto
                    for (ProjectRolesEntity item : neededRoles) { //para cada rol requerido por el proyecto, definir su estructura
                        roleWorker = new RoleWorker();

                        roleWorker.setRole(item.getRole()); //establecer rol
                        roleWorker.setWorkers(new ArrayList<>());
                        roleWorker.setFixedWorkers(new ArrayList<>());
                        roleWorker.setNeededWorkers(item.getAmountWorkersRole()); //cantidad de personas necesarias para este rol en el projecto

                        List<AssignedRoleEntity> as = lastCycle.getAssignedRoleList(); //obtener lista de roles que ya fueron asignados al ciclo actual
                        for (AssignedRoleEntity ar : as) { //para cada rol asignado al ciclo actual
                            if (ar.getStatus().equals(Status.ACTIVE)) { //si se encuentra activo el proyecto
                                roleWorker.getFixedWorkers().add(ar.getPerson()); //agregar la persona a la lista
                                roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() - 1);//decrementar las personas requeridas
                            }
                        }

                        for (FixedWorker fixedWorker : parameters.getFixedWorkers()) {
                            if (fixedWorker.getProject().getId().equals(project.getId())) { //es el projecto al que se fijo?
                                if (fixedWorker.getRole().getId().equals(item.getRole().getId())) { //es el rol ?
                                    roleWorker.getFixedWorkers().add(fixedWorker.getBoss()); //agregar la persona a la lista 
                                    roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() - 1); //decrementar las personas requeridas
                                }
                            }
                        }

                        roleWorkers.add(roleWorker); // agregar el rol con sus datos a la configuracion del proyecto actual
                    }

                    projectRole.setRoleWorkers(roleWorkers); //establecer roles-personas de los proyectos-roles
                    projectRoles.add(projectRole); //agregar proyecto-rol a la lista de proyectos-roles
                    i++;
                }
                initialSolution.getCode().addAll(projectRoles); //codificar el problema
            }
        }
        return initialSolution;
    }

    /**
     * Retorna el ultimo ciclo dado un proyecto (retorna el que no tiene fecha
     * de fin)
     *
     */
    public static CycleEntity lastProjectCycle(ProjectEntity project) {
        CycleEntity cycle = null;
        List<CycleEntity> cycleList = project.getCycleList();

        int i = 0;
        boolean found = false;
        while (i < cycleList.size() && !found) {
            if (cycleList.get(i).getEndDate() == null) {
                cycle = cycleList.get(i);
                found = true;
            }
            i++;
        }
        return cycle;
    }

    public static ProjectRoleState getInitialMinimunRolesSolution(TeamFormationParameters parameters) {
        return new ProjectRoleState();
    }
}
