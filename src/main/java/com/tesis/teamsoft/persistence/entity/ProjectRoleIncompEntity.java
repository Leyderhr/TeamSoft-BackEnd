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
@Table(name = "project_role_incomp")
@Check(constraints = "role_a_fk != role_b_fk")
public class ProjectRoleIncompEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projectRoleIncompSeq")
    @SequenceGenerator(name = "projectRoleIncompSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Cycle is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "cycle_fk", nullable = false)
    private CycleEntity cycles;

    @NotNull(message = "Role A is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_a_fk", nullable = false)
    private RoleEntity roleA;

    @NotNull(message = "Role B is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_b_fk", nullable = false)
    private RoleEntity roleB;

    @Override
    public boolean equals(Object object) {
        if (object instanceof ProjectRoleIncompEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}