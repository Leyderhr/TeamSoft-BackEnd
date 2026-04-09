package com.tesis.teamsoft.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.tesis.teamsoft.persistence.entity.auxiliary.Roles;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_role")
public class UserRoleEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userRoleSeq")
    @SequenceGenerator(name = "userRoleSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Role name is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Roles name;
}