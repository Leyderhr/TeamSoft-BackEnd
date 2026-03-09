package com.tesis.teamsoft.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "person_conflict")
@Check(constraints = "person_fk != person_conflict_fk")
public class PersonConflictEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personConflictSeq")
    @SequenceGenerator(name = "personConflictSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Index is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "index_fk", nullable = false)
    private ConflictIndexEntity index;

    @NotNull(message =  "Person in conflict is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "person_conflict_fk", nullable = false)
    private PersonEntity personConflict;

    @NotNull(message =  "Person is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "person_fk", nullable = false)
    private PersonEntity person;

    @Override
    public boolean equals(Object object) {
        if (object instanceof PersonConflictEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}