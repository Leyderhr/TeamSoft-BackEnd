package com.tesis.teamsoft.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "competence_dimension")
public class CompetenceDimensionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "competenceDimensionSeq")
    @SequenceGenerator(name = "competenceDimensionSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Competence is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "competence_fk", nullable = false)
    private CompetenceEntity competence;

    @NotNull(message = "Level is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "levels_fk", nullable = false)
    private LevelsEntity level;

    @Override
    public boolean equals(Object object) {
        if (object instanceof CompetenceDimensionEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}