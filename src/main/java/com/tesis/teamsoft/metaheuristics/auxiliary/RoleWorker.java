package com.tesis.teamsoft.metaheuristics.auxiliary;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.entity.RoleEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class RoleWorker {

    private RoleEntity role;
    private Long neededWorkers;
    private List<PersonEntity> workers;
    private ArrayList<Object> workersEvaluation;
    private List<PersonEntity> fixedWorkers; //trabajadores que fueron fijados en la pantalla

    public RoleWorker(RoleEntity role, List<PersonEntity> workers, Long neededWorkers, List<PersonEntity> fixedWorkers) {
        super();
        this.role = role;
        this.workers = workers;
        this.neededWorkers = neededWorkers;
        this.fixedWorkers = fixedWorkers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleWorker that = (RoleWorker) o;
        return Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }
}
