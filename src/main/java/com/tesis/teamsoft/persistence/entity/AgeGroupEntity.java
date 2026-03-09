package com.tesis.teamsoft.persistence.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "age_group")
@Check(constraints = "max_age >= min_age")
public class AgeGroupEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ageGroupSeq")
    @SequenceGenerator(name = "ageGroupSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Age group name is required")
    @Column(name = "age_group_name", nullable = false, unique = true)
    private String ageGroupName;

    @NotNull(message = "Maximum age is required")
    @Min(value = 0, message = "Maximum age must be at least 0")
    @Max(value = 150, message = "Maximum age cannot exceed 150")
    @Column(name = "max_age", nullable = false)
    private int maxAge;

    @NotNull(message = "Minimum age is required")
    @Min(value = 0, message = "Minimum age must be at least 0")
    @Max(value = 150, message = "Minimum age cannot exceed 150")
    @Column(name = "min_age", nullable = false)
    private int minAge;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ageGroup")
    private List<PersonEntity> personList;


    @Override
    public boolean equals(Object object) {
        if(object instanceof AgeGroupEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
