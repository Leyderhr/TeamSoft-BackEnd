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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cycle")
@Check(constraints = "end_date IS NULL OR end_date >= begin_date")
public class CycleEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cycleSeq")
    @SequenceGenerator(name = "cycleSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Begin date is required")
    @Column(name = "begin_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date beginDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @NotNull(message = "Project is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_fk", nullable = false)
    private ProjectEntity project;

    @NotNull(message = "Project structure is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_structure_fk", nullable = false)
    private ProjectStructureEntity projectStructure;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cycles")
    private List<AssignedRoleEntity> assignedRoleList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cycles")
    private List<ProjectRoleIncompEntity> projectRoleIncompList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cycles")
    private List<RolePersonEvalEntity> roleEvaluationList;

    // Constructor personalizado
    public CycleEntity(ProjectEntity project, ProjectStructureEntity projectStructure) {
        this.beginDate = project.getInitialDate();
        this.endDate = project.getEndDate();
        this.project = project;
        this.projectStructure = projectStructure;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof CycleEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}