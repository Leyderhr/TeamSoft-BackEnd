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
@Table(name = "person_test")
public class PersonTestEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personTestSeq")
    @SequenceGenerator(name = "personTestSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull
    @Column(name = "e_s", nullable = false)
    @Check(constraints = "e_s IN ('P', 'E', 'I')")
    private Character eS;

    @NotNull
    @Column(name = "i_m", nullable = false)
    @Check(constraints = "i_d IN ('P', 'E', 'I')")
    private Character iM;

    @NotNull
    @Column(name = "c_o", nullable = false)
    @Check(constraints = "c_o IN ('P', 'E', 'I')")
    private Character cO;

    @NotNull
    @Column(name = "i_s", nullable = false)
    @Check(constraints = "i_s IN ('P', 'E', 'I')")
    private Character iS;

    @NotNull
    @Column(name = "c_e", nullable = false)
    @Check(constraints = "c_e IN ('P', 'E', 'I')")
    private Character cE;

    @NotNull
    @Column(name = "i_r", nullable = false)
    @Check(constraints = "i_r IN ('P', 'E', 'I')")
    private Character iR;

    @NotNull
    @Column(name = "m_e", nullable = false)
    @Check(constraints = "m_e IN ('P', 'E', 'I')")
    private Character mE;

    @NotNull
    @Column(name = "c_h", nullable = false)
    @Check(constraints = "c_h IN ('P', 'E', 'I')")
    private Character cH;

    @NotNull
    @Column(name = "i_f", nullable = false)
    @Check(constraints = "i_f IN ('P', 'E', 'I')")
    private Character iF;

    @NotNull(message = "MBTI result is required")
    @Column(name = "mbti_type;", nullable = false)
    @Check(constraints = "tipo_m_b IN ('INTJ', 'INTP', 'ISTJ', 'ISTP', 'INFJ', 'INFP', 'ISFJ', 'ISFP', 'ENTJ', 'ENTP', 'ESTJ', 'ESTP', 'ENFJ', 'ENFP', 'ESFJ', 'ESFP')")
    private String mbtiType;

    @NotNull(message = "Person is required")
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "person_fk", nullable = false)
    private PersonEntity person;

    @Override
    public boolean equals(Object object) {
        if (object instanceof PersonTestEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}