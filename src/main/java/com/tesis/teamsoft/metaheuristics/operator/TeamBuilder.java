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
                    project.setState(parameters.getProjects().get(i).getProject().getState());
                    project.setCycleList(parameters.getProjects().get(i).getProject().getCycleList());
                    project.setEndDate(parameters.getProjects().get(i).getProject().getEndDate());
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
                            // Solo fijar a la persona en ESTE rol si su asignación activa es para el MISMO rol.
                            // Sin este filtro se agregaba cada persona activa a TODOS los roles, dejando
                            // neededWorkers negativo y bloqueando la plantilla (siempre la misma propuesta).
                            if (ar.getStatus().equals(Status.ACTIVE)
                                    && ar.getRole() != null
                                    && ar.getRole().getId().equals(item.getRole().getId())) {
                                if (!containsPersonId(roleWorker.getFixedWorkers(), ar.getPerson())) { //evitar duplicados
                                    roleWorker.getFixedWorkers().add(ar.getPerson()); //agregar la persona a la lista
                                    roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() - 1);//decrementar las personas requeridas
                                }
                            }
                        }

                        for (FixedWorker fixedWorker : parameters.getFixedWorkers()) {
                            if (fixedWorker.getProject().getId().equals(project.getId())) { //es el projecto al que se fijo?
                                if (fixedWorker.getRole().getId().equals(item.getRole().getId())) { //es el rol ?
                                    // No agregar dos veces a la misma persona (p.ej. si ya estaba asignada
                                    // activamente Y se fija desde la pantalla): un duplicado provoca que
                                    // WorkerNotRepeatedInSameRole la marque inválida y la reparación falle.
                                    if (!containsPersonId(roleWorker.getFixedWorkers(), fixedWorker.getBoss())) {
                                        roleWorker.getFixedWorkers().add(fixedWorker.getBoss()); //agregar la persona a la lista
                                        roleWorker.setNeededWorkers(roleWorker.getNeededWorkers() - 1); //decrementar las personas requeridas
                                    }
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
     * Indica si una persona (por id) ya está presente en la lista dada.
     * Se usa para evitar fijar a la misma persona más de una vez en un rol.
     */
    private static boolean containsPersonId(List<PersonEntity> people, PersonEntity person) {
        if (person == null || person.getId() == null) {
            return false;
        }
        for (PersonEntity p : people) {
            if (p != null && person.getId().equals(p.getId())) {
                return true;
            }
        }
        return false;
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
