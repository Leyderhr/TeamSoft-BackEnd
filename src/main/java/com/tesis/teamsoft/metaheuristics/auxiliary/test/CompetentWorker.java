package com.tesis.teamsoft.metaheuristics.auxiliary.test;

import com.tesis.teamsoft.persistence.entity.PersonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompetentWorker { //REVIEW

    private PersonEntity worker;
    private long evaluation;
}
