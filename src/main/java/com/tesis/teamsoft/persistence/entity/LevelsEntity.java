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
@Table(name = "levels")
public class LevelsEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "levelsSeq")
    @SequenceGenerator(name = "levelsSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Levels is required")
    @Column(nullable = false, columnDefinition = "bigint check (levels >= 0)")
    private long levels;

    @NotNull(message = "Significance is required")
    @Column(nullable = false, unique = true)
    private String significance;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "level")
    private List<RoleCompetitionEntity> roleCompetitionList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "level")
    private List<ProjectTechCompetenceEntity> projectTechCompetenceList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "level")
    private List<CompetenceValueEntity> competenceValueList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "level")
    private List<CompetenceDimensionEntity> competenceDimensionList;

    @Override
    public boolean equals(Object object) {
        if (object instanceof LevelsEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}