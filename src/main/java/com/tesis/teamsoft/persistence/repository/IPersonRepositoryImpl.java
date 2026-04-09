package com.tesis.teamsoft.persistence.repository;

import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.presentation.dto.FilterDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class IPersonRepositoryImpl implements IPersonRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private static final String PERSON = "person";

    public List<PersonEntity> findByFilter(FilterDTO.FilterRequestDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PersonEntity> query = cb.createQuery(PersonEntity.class);
        Root<PersonEntity> root = query.from(PersonEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getRoleInterests() != null && !filter.getRoleInterests().isEmpty()) {
            addRoleInterestsFilter(cb, query, root, predicates, filter.getRoleInterests());
        }

        if (filter.getProjectInterests() != null && !filter.getProjectInterests().isEmpty()) {
            addProjectInterestsFilter(cb, query, root, predicates, filter.getProjectInterests());
        }

        if (filter.getCompetenceLevels() != null && !filter.getCompetenceLevels().isEmpty()) {
            addCompetenceLevelsFilter(cb, query, root, predicates, filter.getCompetenceLevels());
        }

        if (filter.getBelbin() != null && isBelbinFilterActive(filter.getBelbin())) {
            addBelbinFilter(cb, root, predicates, filter.getBelbin());
        }

        if (filter.getMbtiTypes() != null && !filter.getMbtiTypes().isEmpty()) {
            addMbtiFilter(root, predicates, filter.getMbtiTypes());
        }

        if (filter.getAge() != null) {
            addAgeFilter(cb, root, predicates, filter.getAge());
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }


    private void addRoleInterestsFilter(CriteriaBuilder cb, CriteriaQuery<?> query,
                                        Root<PersonEntity> root, List<Predicate> predicates,
                                        List<FilterDTO.PersonalInterestFilterDTO> roleFilters) {
        for (FilterDTO.PersonalInterestFilterDTO rf : roleFilters) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<PersonalInterestsEntity> piRoot = subquery.from(PersonalInterestsEntity.class);
            subquery.select(piRoot.get("id"))
                    .where(cb.and(
                            cb.equal(piRoot.get(PERSON), root),
                            cb.equal(piRoot.get("role").get("id"), rf.getRoleId()),
                            cb.equal(piRoot.get("preference"), rf.getPreference())
                    ));
            predicates.add(cb.exists(subquery));
        }
    }

    private void addProjectInterestsFilter(CriteriaBuilder cb, CriteriaQuery<?> query,
                                           Root<PersonEntity> root, List<Predicate> predicates,
                                           List<FilterDTO.PersonalProjectInterestFilterDTO> projectFilters) {
        for (FilterDTO.PersonalProjectInterestFilterDTO pf : projectFilters) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<PersonalProjectInterestsEntity> ppiRoot = subquery.from(PersonalProjectInterestsEntity.class);
            subquery.select(ppiRoot.get("id"))
                    .where(cb.and(
                            cb.equal(ppiRoot.get(PERSON), root),
                            cb.equal(ppiRoot.get("project").get("id"), pf.getProjectId()),
                            cb.equal(ppiRoot.get("preference"), pf.getPreference())
                    ));
            predicates.add(cb.exists(subquery));
        }
    }

    private void addCompetenceLevelsFilter(CriteriaBuilder cb, CriteriaQuery<?> query,
                                           Root<PersonEntity> root, List<Predicate> predicates,
                                           List<FilterDTO.CompetenceValueFilterDTO> competenceFilters) {
        for (FilterDTO.CompetenceValueFilterDTO cf : competenceFilters) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<CompetenceValueEntity> cvRoot = subquery.from(CompetenceValueEntity.class);
            subquery.select(cvRoot.get("id"))
                    .where(cb.and(
                            cb.equal(cvRoot.get(PERSON), root),
                            cb.equal(cvRoot.get("competence").get("id"), cf.getCompetenceId()),
                            cb.equal(cvRoot.get("level").get("id"), cf.getLevelId())
                    ));
            predicates.add(cb.exists(subquery));
        }
    }

    private boolean isBelbinFilterActive(FilterDTO.BelbinFilterDTO belbin) {
        return belbin.getIF() != null || belbin.getIS() != null || belbin.getIM() != null ||
                belbin.getCE() != null || belbin.getES() != null || belbin.getME() != null ||
                belbin.getCO() != null || belbin.getCH() != null || belbin.getIR() != null;
    }

    private void addBelbinFilter(CriteriaBuilder cb, Root<PersonEntity> root,
                                 List<Predicate> predicates, FilterDTO.BelbinFilterDTO belbin) {
        Join<PersonEntity, PersonTestEntity> testJoin = root.join("personTest", JoinType.INNER);
        if (belbin.getIF() != null) predicates.add(cb.equal(testJoin.get("iF"), belbin.getIF()));
        if (belbin.getIS() != null) predicates.add(cb.equal(testJoin.get("iS"), belbin.getIS()));
        if (belbin.getIM() != null) predicates.add(cb.equal(testJoin.get("iM"), belbin.getIM()));
        if (belbin.getCE() != null) predicates.add(cb.equal(testJoin.get("cE"), belbin.getCE()));
        if (belbin.getES() != null) predicates.add(cb.equal(testJoin.get("eS"), belbin.getES()));
        if (belbin.getME() != null) predicates.add(cb.equal(testJoin.get("mE"), belbin.getME()));
        if (belbin.getCO() != null) predicates.add(cb.equal(testJoin.get("cO"), belbin.getCO()));
        if (belbin.getCH() != null) predicates.add(cb.equal(testJoin.get("cH"), belbin.getCH()));
        if (belbin.getIR() != null) predicates.add(cb.equal(testJoin.get("iR"), belbin.getIR()));
    }

    private void addMbtiFilter(Root<PersonEntity> root,
                               List<Predicate> predicates, List<String> mbtiTypes) {
        Join<PersonEntity, PersonTestEntity> testJoin = root.join("personTest", JoinType.INNER);
        predicates.add(testJoin.get("mbtiType").in(mbtiTypes));
    }

    private void addAgeFilter(CriteriaBuilder cb, Root<PersonEntity> root,
                              List<Predicate> predicates, FilterDTO.AgeFilterDTO ageFilter) {
        if (ageFilter.getMinAge() != null || ageFilter.getMaxAge() != null) {
            LocalDate now = LocalDate.now();
            Date minDate = null;
            Date maxDate = null;

            if (ageFilter.getMaxAge() != null) {
                LocalDate minBirth = now.minusYears(ageFilter.getMaxAge());
                minDate = Date.from(minBirth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
            if (ageFilter.getMinAge() != null) {
                LocalDate maxBirth = now.minusYears(ageFilter.getMinAge()).minusDays(1);
                maxDate = Date.from(maxBirth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            if (minDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("birthDate"), minDate));
            }
            if (maxDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("birthDate"), maxDate));
            }
        }
    }
}