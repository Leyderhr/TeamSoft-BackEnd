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
@Table(name = "competence")
public class CompetenceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "competenceSeq")
    @SequenceGenerator(name = "competenceSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Competition name is required")
    @Column(name = "competition_name", nullable = false, unique = true)
    private String competitionName;

    @NotNull(message = "Description is required")
    @Column(nullable = false, unique = true)
    private String description;

    @Column(name = "technical", nullable = false)
    private Boolean technical;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competence")
    private List<RoleCompetitionEntity> roleCompetitionList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competence")
    private List<ProjectTechCompetenceEntity> projectTechCompetenceList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competence")
    private List<CompetenceValueEntity> competenceValueList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competence")
    private List<CompetenceDimensionEntity> competenceDimensionList;

    @Override
    public boolean equals(Object object) {
        if (object instanceof CompetenceEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}