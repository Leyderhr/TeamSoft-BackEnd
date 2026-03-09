package com.tesis.teamsoft.persistence.entity;

import com.tesis.teamsoft.persistence.entity.auxiliar.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "assigned_role")
@Check(constraints = "end_date IS NULL OR end_date > begin_date")
public class AssignedRoleEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assignedRoleSeq")
    @SequenceGenerator(name = "assignedRoleSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1024)
    private Status status;

    @NotBlank(message = "Observation is required")
    @Column(nullable = false)
    private String observation;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    @Future(message = "End date must be in the future")
    private Date endDate;

    @NotNull(message = "Begin date is required")
    @Column(name = "begin_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @PastOrPresent(message = "Begin date must be in the past or present")
    private Date beginDate;

    @NotNull(message = "Cycle is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "cycles_fk", nullable = false)
    private CycleEntity cycles;

    @NotNull(message = "Role is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "roles_fk", nullable = false)
    private RoleEntity role;

    @NotNull(message = "Person is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "person_fk", nullable = false)
    private PersonEntity person;

    @Override
    public boolean equals(Object object) {
        if (object instanceof AssignedRoleEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}