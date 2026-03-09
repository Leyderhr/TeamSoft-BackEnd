package com.tesis.teamsoft.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role_evaluation")
public class RoleEvaluationEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleEvaluationSeq")
    @SequenceGenerator(name = "roleEvaluationSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Levels value is required")
    @Column(nullable = false, columnDefinition = "float check (levels >= 0)")
    private float levels;

    @NotNull(message = "Significance is required")
    @Column(nullable = false, unique = true)
    private String significance;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roleEvaluation")
    private List<ProjectEntity> projectsList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roleEvaluation")
    private List<RolePersonEvalEntity> roleEvaluationList;

    @Override
    public boolean equals(Object object) {
        if (object instanceof RoleEvaluationEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}