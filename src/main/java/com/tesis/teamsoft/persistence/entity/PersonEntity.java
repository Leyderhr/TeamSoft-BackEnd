package com.tesis.teamsoft.persistence.entity;

import com.tesis.teamsoft.persistence.entity.auxiliar.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "person")
@Check(constraints = "birth_date <= in_date")
public class PersonEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personSeq")
    @SequenceGenerator(name = "personSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Person name is required")
    @Size(min = 1, max = 1024, message = "Person name must be between 1 and 1024 characters")
    @Column(name = "person_name", nullable = false, length = 1024)
    private String personName;

    @NotNull(message = "ID card is required")
    @Size(min = 1, max = 1024, message = "ID card must be between 1 and 1024 characters")
    @Column(name = "id_card", nullable = false, length = 1024)
    private String card;

    @NotNull(message = "Surname is required")
    @Size(max = 1024, message = "Surname cannot exceed 1024 characters")
    @Column(name = "sur_name", nullable = false, length = 1024)
    private String surName;

    @NotNull(message = "Address is required")
    @Size(max = 1024, message = "Address cannot exceed 1024 characters")
    @Column(nullable = false, length = 1024)
    private String address;

    @NotNull(message = "Phone is required")
    @Size(max = 1024, message = "Phone cannot exceed 1024 characters")
    @Column(nullable = false, length = 1024)
    private String phone;

    @NotNull(message = "Sex is required")
    @Column(nullable = false, columnDefinition = "char(1) check (sex in ('M','F', 'O'))")
    private Character sex;

    @NotNull(message = "Email is required")
    @Size(max = 1024, message = "Email cannot exceed 1024 characters")
    @Column(nullable = false, length = 1024)
    private String email;

    @NotNull(message = "In date is required")
    @Column(name = "in_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date inDate;

    @NotNull(message = "Workload is required")
    @Column(nullable = false, columnDefinition = "float check (workload >= 0)")
    private Float workload;

    @NotNull(message = "Experience is required")
    @Column(nullable = false, columnDefinition = "int check (experience >= 0)")
    private Integer experience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1024)
    private Status status;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    // Relaciones OneToMany
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private List<AssignedRoleEntity> assignedRoleList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
    private List<CompetenceValueEntity> competenceValueList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
    private List<PersonConflictEntity> personConflictList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personConflict")
    private List<PersonConflictEntity> personConflictWithList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
    private List<PersonalInterestsEntity> personalInterestsList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
    private List<PersonalProjectInterestsEntity> personalProjectInterestsList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private List<RolePersonEvalEntity> roleEvaluationList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private List<RoleExperienceEntity> roleExperienceList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
    private PersonTestEntity personTest;
    
    @ManyToOne()
    @JoinColumn(name = "county_fk")
    private CountyEntity county;

    @ManyToOne()
    @JoinColumn(name = "race_fk")
    private RaceEntity race;

    @ManyToOne()
    @JoinColumn(name = "group_fk")
    private PersonGroupEntity group;

    @ManyToOne()
    @JoinColumn(name = "nacionality_fk")
    private NacionalityEntity nacionality;

    @ManyToOne()
    @JoinColumn(name = "religion_fk")
    private ReligionEntity religion;

    @ManyToOne()
    @JoinColumn(name = "age_group_fk")
    private AgeGroupEntity ageGroup;


    public int getAge() {
        int age = 0;
        if (birthDate != null) {
            LocalDate firstDate;
            if (birthDate instanceof java.sql.Date sqlDate) {
                firstDate = sqlDate.toLocalDate();
            } else {
                firstDate = birthDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
            LocalDate secondDate = LocalDate.now();
            age = Period.between(firstDate, secondDate).getYears();
        }
        return age;
    }

    public RoleExperienceEntity getRoleExperience(Long idRol) {
        RoleExperienceEntity xp = new RoleExperienceEntity();
        for (RoleExperienceEntity roleExperience : this.roleExperienceList) {
            if (roleExperience.getRole().getId().equals(idRol)) {
                xp = roleExperience;
                break;
            }
        }
        return xp;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof PersonEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}