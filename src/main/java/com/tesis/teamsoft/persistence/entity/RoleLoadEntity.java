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
@Table(name = "role_load")
public class RoleLoadEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleLoadSeq")
    @SequenceGenerator(name = "roleLoadSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Value is required")
    @Column(nullable = false, columnDefinition = "float check (value >= 0)")
    private float value;

    @NotNull(message = "Significance is required")
    @Column(nullable = false, unique = true)
    private String significance;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roleLoad")
    private List<ProjectRolesEntity> projectRolesList;

    @Override
    public boolean equals(Object object) {
        if (object instanceof RoleLoadEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}