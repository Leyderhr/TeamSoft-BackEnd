package com.tesis.teamsoft.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "role_experience")
public class RoleExperienceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleExperienceSeq")
    @SequenceGenerator(name = "roleExperienceSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Indexes value is required")
    @Column(nullable = false, columnDefinition = "float check (indexes >= 0)")
    private float indexes;

    @NotNull(message = "Role is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_fk", nullable = false)
    private RoleEntity role;

    @NotNull(message = "Person is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "person_fk", nullable = false)
    private PersonEntity person;

    @Override
    public boolean equals(Object object) {
        if (object instanceof RoleExperienceEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}