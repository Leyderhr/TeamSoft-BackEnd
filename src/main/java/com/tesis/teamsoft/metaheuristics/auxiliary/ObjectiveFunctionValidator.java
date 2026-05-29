package com.tesis.teamsoft.metaheuristics.auxiliary;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.persistence.entity.PersonEntity;
import com.tesis.teamsoft.persistence.repository.IAgeGroupRepository;
import com.tesis.teamsoft.persistence.repository.INacionalityRepository;
import com.tesis.teamsoft.persistence.repository.IReligionRepository;
import com.tesis.teamsoft.pojos.TeamFormationParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ObjectiveFunctionValidator {

    private final INacionalityRepository nacionalityRepository;
    private final IReligionRepository religionRepository;
    private final IAgeGroupRepository ageGroupRepository;

    private static final double NULL_THRESHOLD = 0.20;

    public void validate(TeamFormationParameters parameters) {
        if (parameters == null) {
            throw new BusinessRuleException("The parameters must not be null");
        }

        validateNomenclatorsExist(parameters);
        validateNullPercentage(parameters);
    }

    private void validateNomenclatorsExist(TeamFormationParameters parameters) {
        if (isAnyNationalityObjectiveActive(parameters) && nacionalityRepository.count() == 0) {
            throw new BusinessRuleException(
                    "No nationalities are defined in the system. Cannot use any nationality objective function.");
        }

        if (isAnyReligionObjectiveActive(parameters) && religionRepository.count() == 0) {
            throw new BusinessRuleException(
                    "No religions are defined in the system. Cannot use any religion objective function.");
        }

        if (isAnyAgeGroupObjectiveActive(parameters) && ageGroupRepository.count() == 0) {
            throw new BusinessRuleException(
                    "No age groups are defined in the system. Cannot use any age group objective function.");
        }
    }

    private void validateNullPercentage(TeamFormationParameters parameters) {
        List<PersonEntity> searchArea = parameters.getSearchArea();
        if (searchArea == null || searchArea.isEmpty()) {
            return;
        }

        int total = searchArea.size();
        int nullNacionalidad = 0;
        int nullReligion = 0;
        int nullAgeGroup = 0;

        for (PersonEntity person : searchArea) {
            if (person.getNacionality() == null) nullNacionalidad++;
            if (person.getReligion() == null) nullReligion++;
            if (person.getAgeGroup() == null) nullAgeGroup++;
        }

        double percentNacionalidad = (double) nullNacionalidad / total;
        double percentReligion = (double) nullReligion / total;
        double percentAgeGroup = (double) nullAgeGroup / total;

        int requiredNonNullPercent = (int) ((1 - NULL_THRESHOLD) * 100);

        if (isAnyNationalityObjectiveActive(parameters) && percentNacionalidad >= NULL_THRESHOLD) {
            throw new BusinessRuleException(
                    String.format("%.2f%% of workers have no assigned nationality (minimum required: %d%%). Cannot use nationality objective functions.",
                            percentNacionalidad * 100, requiredNonNullPercent));
        }

        if (isAnyReligionObjectiveActive(parameters) && percentReligion >= NULL_THRESHOLD) {
            throw new BusinessRuleException(
                    String.format("%.2f%% of workers have no assigned religion. Cannot use religion objective functions.",
                            percentReligion * 100));
        }

        if (isAnyAgeGroupObjectiveActive(parameters) && percentAgeGroup >= NULL_THRESHOLD) {
            throw new BusinessRuleException(
                    String.format("%.2f%% of workers have no assigned age group. Cannot use age group objective functions.",
                            percentAgeGroup * 100));
        }
    }

    private boolean isAnyNationalityObjectiveActive(TeamFormationParameters params) {
        return params.isHeterogeneousTeams() || params.isHomogeneousTeams() ||
                params.isBalanceHeterogeneousTeams() || params.isBalanceHomogeneousTeams();
    }

    private boolean isAnyReligionObjectiveActive(TeamFormationParameters params) {
        return params.isMaxReligion() || params.isMinReligion() ||
                params.isBalanceMaximizeReligion() || params.isBalanceMinimizeReligion();
    }

    private boolean isAnyAgeGroupObjectiveActive(TeamFormationParameters params) {
        return params.isMaxAgeHeterogeneity() || params.isMinAgeHomogeneity() ||
                params.isBalanceMaximizeAgeHeterogeneity() || params.isBalanceMinimizeAgeHomogeneity();
    }
}