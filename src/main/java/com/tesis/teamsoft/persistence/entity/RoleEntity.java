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
@Table(name = "role")
public class RoleEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleSeq")
    @SequenceGenerator(name = "roleSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Role name is required")
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    @NotNull(message = "Role description is required")
    @Column(name = "role_desc", nullable = false, unique = true)
    private String roleDesc;

    @NotNull(message = "Impact is required")
    @Column(nullable = false, columnDefinition = "float check (impact >= 0)")
    private float impact;

    @NotNull(message = "Is boss flag is required")
    @Column(name = "is_boss", nullable = false)
    private boolean isBoss;

    // Relación de roles incompatibles
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "incompatible_roles",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "incompatible_role_id")
    )
    private List<RoleEntity> incompatibleRoles;

    @ManyToMany(mappedBy = "incompatibleRoles", fetch = FetchType.LAZY)
    private List<RoleEntity> incompatibleWith;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<AssignedRoleEntity> assignedRoleList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role", orphanRemoval = true)
    private List<RoleCompetitionEntity> roleCompetitionList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<RoleExperienceEntity> roleExperienceList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roleA")
    private List<ProjectRoleIncompEntity> projectRoleIncompListA;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roleB")
    private List<ProjectRoleIncompEntity> projectRoleIncompListB;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<PersonalInterestsEntity> personalInterestsList;

    @OneToMany(mappedBy = "roles")
    private List<RolePersonEvalEntity> rolePersonEvalList;

    @Override
    public boolean equals(Object object) {
        if (object instanceof RoleEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}