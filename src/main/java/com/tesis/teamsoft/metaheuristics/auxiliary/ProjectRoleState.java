package com.tesis.teamsoft.metaheuristics.auxiliary;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import metaheuristics.generators.GeneratorType;

import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

public class ProjectRoleState extends State {

    public ProjectRoleState(List<Object> code, List<Double> evaluation, GeneratorType typeGenerator) {
    }

    @Override
    public ProjectRoleState clone() {
        return new ProjectRoleState(this.code, this.evaluation, this.typeGenerator);
    }

    public ProjectRoleState(ArrayList<Object> code, List<Double> evaluation, GeneratorType typeGenerator) {
        setCode(code);
        if (evaluation != null) {
            this.evaluation = new ArrayList<>(evaluation);
        } else {
            this.evaluation = new ArrayList<>();
        }
        this.typeGenerator = typeGenerator;
    }

    public ProjectRoleState() {
        super();
    }

    public ProjectRoleState(ArrayList<Object> code) {
        super(code);
    }

    public ProjectRoleState(State ps) {
        super(ps);
    }

    @Override
    public void setCode(ArrayList<Object> code) {
        ArrayList<Object> list = new ArrayList<>();
        for (Object o : code) {
            ProjectRole projectRole = (ProjectRole) o;
            List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
            List<RoleWorker> copyRoleWorkers = getRoleWorkers(roleWorkers);
            ProjectRole copyProjectRole = new ProjectRole(projectRole.getProject(), projectRole.getProjectEvaluation(), copyRoleWorkers);
            list.add(copyProjectRole);
        }
        this.code = list;
    }


    @Override
	public boolean Comparator(State state){
            boolean found = false;
            int length = this.getCode().size();
            int count = 0;
		for (int j = 0; j < length; j++) {
			List<RoleWorker> roleWorkers = ((ProjectRole)state.getCode().get(j)).getRoleWorkers();
			int totalRoles = ((ProjectRole)state.getCode().get(j)).getRoleWorkers().size();
			int countRoles = 0;
			for (int i = 0; i < totalRoles; i++) {
                for (RoleWorker roleWorker : roleWorkers) {
                    int countWorkers = getCountWorkers(roleWorker, j, i);
                    if (countWorkers == roleWorker.getWorkers().size()) {
                        countRoles++;
                    }
                }
			}
			if(countRoles == totalRoles){
				count++;
			}
		}
		if(count == length){
		    found = true;
		}
		return found;
	}

    private int getCountWorkers(RoleWorker roleWorker, int j, int i) {
        int countWorkers = 0;
        for (int k = 0; k < roleWorker.getWorkers().size(); k++) {
            if (roleWorker.getWorkers().size() == ((ProjectRole) this.getCode().get(j)).getRoleWorkers().get(i).getWorkers().size()) {
                PersonEntity thisWorker = ((ProjectRole) this.getCode().get(j)).getRoleWorkers().get(i).getWorkers().get(k);
                if (((PersonEntity) roleWorker.getWorkers().toArray()[k]).getId().equals(thisWorker.getId())) {
                    countWorkers++;
                }
            }
        }
        return countWorkers;
    }

    private static List<RoleWorker> getRoleWorkers(List<RoleWorker> roleWorkers) {
        List<RoleWorker> copyRoleWorkers = new ArrayList<>(roleWorkers.size());

        for (RoleWorker roleWorker : roleWorkers) {
            List<PersonEntity> workers = roleWorker.getWorkers();
            List<PersonEntity> copyWorkers = new ArrayList<>(roleWorker.getWorkers().size());

            copyWorkers.addAll(workers);
            RoleWorker copyRoleworker = new RoleWorker(roleWorker.getRole(), copyWorkers, roleWorker.getNeededWorkers(), roleWorker.getFixedWorkers());
            copyRoleWorkers.add(copyRoleworker);
        }
        return copyRoleWorkers;
    }
}
