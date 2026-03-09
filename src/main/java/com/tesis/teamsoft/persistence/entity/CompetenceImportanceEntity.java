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
@Table(name = "competence_importance")
public class CompetenceImportanceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "competenceImportanceSeq")
    @SequenceGenerator(name = "competenceImportanceSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Level name is required")
    @Column(nullable = false, columnDefinition = "bigint check (levels >= 0)")
    private long levels;

    @NotNull(message = "Significance is required")
    @Column(nullable = false, unique = true)
    private String significance;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competenceImportance")
    private List<RoleCompetitionEntity> roleCompetitionList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competenceImportance")
    private List<ProjectTechCompetenceEntity> projectTechCompetenceList;

    @Override
    public boolean equals(Object object) {
        if (object instanceof CompetenceImportanceEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}