package com.tesis.teamsoft.metaheuristics.auxiliary;

import com.tesis.teamsoft.persistence.entity.AgeGroupEntity;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.repository.IAgeGroupRepository;
import com.tesis.teamsoft.persistence.repository.IPersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateAgeGroupUtil {

    private final IPersonRepository personRepository;
    private final IAgeGroupRepository ageGroupRepository;

    @Transactional
    public void assignAgeGroups() {
        List<PersonEntity> persons = personRepository.findAll();
        List<AgeGroupEntity> ageGroups = ageGroupRepository.findAll();

        for (PersonEntity person : persons) {
            if (person.getBirthDate() == null) {
                continue;
            }

            int age = person.getAge();
            AgeGroupEntity matchingGroup = ageGroups.stream()
                    .filter(ag -> age >= ag.getMinAge() && age <= ag.getMaxAge())
                    .findFirst()
                    .orElse(null);

            person.setAgeGroup(matchingGroup);
        }
        personRepository.saveAll(persons);
    }
}