package com.tesis.teamsoft.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
@Check(constraints = "end_date IS NULL OR end_date >= initial_date")
public class ProjectEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projectSeq")
    @SequenceGenerator(name = "projectSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Project name is required")
    @Column(name = "project_name", nullable = false, unique = true)
    private String projectName;

    @NotNull(message = "Close flag is required")
    @Column(nullable = false)
    private boolean close;

    @NotNull(message = "Initial date is required")
    @Column(name = "initial_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date initialDate;

    @NotNull(message = "Finalize flag is required")
    @Column(nullable = false)
    private boolean finalize;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @NotNull(message = "Client is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "client_entity_fk", nullable = false)
    private ClientEntity client;

    @NotNull(message = "Role evaluation is required")
    @ManyToOne()
    @JoinColumn(name = "role_eval_fk")
    private RoleEvaluationEntity roleEvaluation;

    @NotNull(message = "Province is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "province_fk", nullable = false)
    private CountyEntity province;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<CycleEntity> cycleList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<PersonalProjectInterestsEntity> personalProjectInterestsList;

    @Override
    public boolean equals(Object object) {
        if (object instanceof ProjectEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void updateCycle(ProjectStructureEntity projectStructure) {
        CycleEntity update = this.cycleList.getFirst();
        update.setBeginDate(getInitialDate());
        update.setEndDate(getEndDate());
        update.setProjectStructure(projectStructure);
    }
}