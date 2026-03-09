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
@Table(name = "conflict_index")
public class ConflictIndexEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conflictIndexSeq")
    @SequenceGenerator(name = "conflictIndexSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Description is required")
    @Column(nullable = false, unique = true)
    private String description;

    @NotNull(message = "Weight is required")
    @Column(nullable = false, columnDefinition = "bigint check (weight >= 0)")
    private long weight;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "index")
    private List<PersonConflictEntity> personConflictList;

    @Override
    public boolean equals(Object object) {
        if (object instanceof ConflictIndexEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}