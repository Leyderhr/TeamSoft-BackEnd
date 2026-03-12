package com.tesis.teamsoft.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeq")
    @SequenceGenerator(name = "userSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Person name is required")
    @Column(name = "person_name", nullable = false)
    private String personName;

    @NotNull(message = "Surname is required")
    @Column(nullable = false)
    private String surname;

    @NotNull(message = "ID card is required")
    @Column(name = "id_card", nullable = false)
    private String card;

    @NotNull(message = "Mail is required")
    @Column(nullable = false, unique = true)
    private String mail;

    @NotNull(message = "Username is required")
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull(message = "Password is required")
    @Size(min = 1, max = 1024, message = "Password must be between 1 and 1024 characters")
    @Column(nullable = false, length = 1024)
    private String password;

    @NotNull(message = "Enabled flag is required")
    @Column(nullable = false)
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = UserRoleEntity.class)
    @JoinTable(
            name = "authorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_role_id")
    )
    private Set<UserRoleEntity> roles;

    @Override
    public boolean equals(Object object) {
        if (object instanceof UserEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}