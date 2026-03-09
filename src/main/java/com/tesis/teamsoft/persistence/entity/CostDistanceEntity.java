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
@Table(name = "cost_distance")
@Check(constraints = "county_a_fk != county_b_fk")
public class CostDistanceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "costDistanceSeq")
    @SequenceGenerator(name = "costDistanceSeq", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Cost distance is required")
    @Column(name = "cost_distance", nullable = false)
    private float costDistance;

    @NotNull(message = "County A is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "county_a_fk", nullable = false)
    private CountyEntity countyA;

    @NotNull(message = "County B is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "county_b_fk", nullable = false)
    private CountyEntity countyB;

    @Override
    public boolean equals(Object object) {
        if (object instanceof CostDistanceEntity other) {
            return this.id != null && other.id != null && this.id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}