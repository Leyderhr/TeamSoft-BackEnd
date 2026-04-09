package com.tesis.teamsoft.metaheuristics.auxiliary;

import java.util.Iterator;

import com.tesis.teamsoft.metaheuristics.operator.TeamBuilder;
import com.tesis.teamsoft.persistence.entity.CycleEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.ProjectEntity;
import com.tesis.teamsoft.persistence.entity.RoleEntity;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class ProjectRole {

    private ProjectEntity project;
    private Double[] projectEvaluation = new Double[12];
    private List<RoleWorker> roleWorkers;

    public ProjectRole(ProjectEntity project, Double[] projectEvaluation, List<RoleWorker> roleWorkers) {
        super();
        this.project = project;
        this.projectEvaluation = projectEvaluation;
        this.roleWorkers = roleWorkers;
    }

    public ProjectRole() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Retorna el ultimo ciclo del proyecto (retorna el que no tiene fecha de
     * fin)
     *
     * @return
     */
    public CycleEntity lastProjectCycle() {
        return TeamBuilder.lastProjectCycle(project);
    }

    /**
     * @param workerToFind persona a buscar
     * @return número de veces que está asignada una persona en un proyecto
     */
    public int getCantOccurenceWorker(PersonEntity workerToFind) {
        return (int) roleWorkers.
                stream().
                map(roleWorker -> roleWorker.getWorkers().stream().filter(workerToFind::equals).findAny()).
                filter(Optional::isPresent).
                count();
    }

    /**
     * busca el rol que está puesto como líder
     */
    public RoleWorker getProjectBoss() {
        for (Iterator<RoleWorker> iterator = roleWorkers.iterator(); iterator.hasNext();) {
            RoleWorker roleWorker = iterator.next();
            if(roleWorker.getRole().isBoss()){
                return roleWorker;
            }
        }
        return null;
    }

    /**
     * obtiene la lista de roles asociados al proyecto
     */
    public List<RoleEntity> getRoles() {
        List<RoleEntity> result = new LinkedList<>();
        roleWorkers.forEach(e -> result.add(e.getRole()));
        return result;
    }

    /**
     * busca los workes con menos roles asignados que los definidos por parámetro
     */
    public List<PersonEntity> findUnreliableWorkers(int cantRoles) {
        List<PersonEntity> result = new LinkedList<>();
        roleWorkers.forEach(roleWorker -> result.
                addAll(roleWorker.
                        getWorkers().
                        stream().
                        filter(worker -> getCantOccurenceWorker(worker) < cantRoles).
                        collect(Collectors.toCollection(LinkedList::new)))
        );
        return result;
    }

    /**
     * busca los workes con más roles asignados que los definidos por parámetro
     */
    public List<PersonEntity> findWorkersAssignedToMoreRolesThanMinimum(int cantRoles) {
        List<PersonEntity> result = new LinkedList<>();
        roleWorkers.forEach(roleWorker -> result.
                addAll(roleWorker.
                        getWorkers().
                        stream().
                        filter(worker -> getCantOccurenceWorker(worker) > cantRoles).
                        collect(Collectors.toCollection(LinkedList::new)))
        );
        return result;
    }

    /**
     * busca los roles asignados de un trabajador
     */
    public List<RoleWorker> findAssignedRoleWorkerByWorker(PersonEntity worker) {
        List<RoleWorker> result = new LinkedList<>();
        roleWorkers.forEach(roleWorker -> {
            if (roleWorker.getWorkers().contains(worker)) {
                result.add(roleWorker);
            }
        });
        return result;
    }
}
