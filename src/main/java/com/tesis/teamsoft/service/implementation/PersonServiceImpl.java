package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.entity.auxiliary.Status;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.presentation.dto.*;
import com.tesis.teamsoft.service.interfaces.IPersonService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements IPersonService {

    private final IPersonRepository personRepository;
    private final ICountyRepository countyRepository;
    private final IRaceRepository raceRepository;
    private final IPersonGroupRepository personGroupRepository;
    private final INacionalityRepository nacionalityRepository;
    private final IReligionRepository religionRepository;
    private final IAgeGroupRepository ageGroupRepository;
    private final ICompetenceRepository competenceRepository;
    private final ILevelsRepository levelsRepository;
    private final IRoleRepository roleRepository;
    private final IProjectRepository projectRepository;
    private final IConflictIndexRepository conflictIndexRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public PersonDTO.PersonResponseDTO savePerson(PersonDTO.PersonCreateDTO personDTO) {
        PersonEntity person = modelMapper.map(personDTO, PersonEntity.class);
        person.setStatus(Status.ACTIVE);
        person.setWorkload(0.0f);

        processSimpleRelations(personDTO, person);
        processAgeGroup(personDTO, person);

        if (personDTO.getCompetenceValues() != null) {
            person.setCompetenceValueList(processCompetenceValues(personDTO.getCompetenceValues(), person));
        }
        if (personDTO.getPersonalInterests() != null) {
            person.setPersonalInterestsList(processPersonalInterests(personDTO.getPersonalInterests(), person));
        }
        if (personDTO.getPersonalProjectInterests() != null) {
            person.setPersonalProjectInterestsList(processPersonalProjectInterests(personDTO.getPersonalProjectInterests(), person));
        }
        if (personDTO.getPersonTest() != null) {
            person.setPersonTest(processPersonTest(personDTO.getPersonTest(), person));
        }
        if (personDTO.getPersonConflicts() != null) {
            person.setPersonConflictList(processPersonConflicts(personDTO.getPersonConflicts(), person));
        }

        return convertToResponseDTO(personRepository.save(person));
    }

    @Override
    @Transactional
    public PersonDTO.PersonResponseDTO updatePerson(PersonDTO.PersonCreateDTO personDTO, Long id) {
        PersonEntity existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PERSON_NOT_FOUND", id));

        mapBasicFields(existingPerson, personDTO, id);
        processSimpleRelations(personDTO, existingPerson);
        processAgeGroup(personDTO, existingPerson);

        if (personDTO.getCompetenceValues() != null) {
            List<CompetenceValueEntity> validatedCompetenceValues = processCompetenceValues(personDTO.getCompetenceValues(), existingPerson);
            syncCompetenceValues(existingPerson, validatedCompetenceValues);
        } else {
            existingPerson.getCompetenceValueList().clear();
        }

        if (personDTO.getPersonalInterests() != null) {
            List<PersonalInterestsEntity> validatedPersonalInterests = processPersonalInterests(personDTO.getPersonalInterests(), existingPerson);
            syncPersonalInterests(existingPerson, validatedPersonalInterests);
        } else {
            existingPerson.getPersonalInterestsList().clear();
        }

        if (personDTO.getPersonalProjectInterests() != null) {
            List<PersonalProjectInterestsEntity> validatedProjectInterests = processPersonalProjectInterests(personDTO.getPersonalProjectInterests(), existingPerson);
            syncPersonalProjectInterests(existingPerson, validatedProjectInterests);
        } else {
            existingPerson.getPersonalProjectInterestsList().clear();
        }

        if (personDTO.getPersonTest() != null) {
            PersonTestEntity personTest = processPersonTest(personDTO.getPersonTest(), existingPerson);
            existingPerson.setPersonTest(personTest);
        } else {
            existingPerson.setPersonTest(null);
        }

        if (personDTO.getPersonConflicts() != null) {
            List<PersonConflictEntity> validatedPersonConflicts = processPersonConflicts(personDTO.getPersonConflicts(), existingPerson);
            syncPersonConflicts(existingPerson, validatedPersonConflicts);
        } else {
            existingPerson.getPersonConflictList().clear();
        }

        return convertToResponseDTO(personRepository.save(existingPerson));
    }

    @Override
    @Transactional
    public String deletePerson(Long id) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PERSON_NOT_FOUND", id));

        if((person.getAssignedRoleList() != null && !person.getAssignedRoleList().isEmpty()) ||
                (person.getRoleEvaluationList() != null && !person.getRoleEvaluationList().isEmpty()))
            throw new BusinessRuleException("ERR_PERSON_CANT_BE_DELETED");

        personRepository.deleteById(id);
        return "PERSON_SUCCESSFULLY_DELETED";
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonDTO.PersonResponseDTO> findAllPerson() {
        return personRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonDTO.PersonResponseDTO> findAllByOrderByIdAsc() {
        return personRepository.findAllByOrderByIdAsc().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDTO.PersonResponseDTO findPersonById(Long id) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PERSON_NOT_FOUND", id));
        return convertToResponseDTO(person);
    }

    @Transactional
    public PersonDTO.PersonResponseDTO patchCompetencesAndConflicts(PersonDTO.PersonCompetenceConflictPatchDTO patchDTO, Long id) {
        PersonEntity existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PERSON_NOT_FOUND", id));

        if (patchDTO.getCompetenceValues() != null) {
            List<CompetenceValueEntity> validatedCompetenceValues = processCompetenceValues(patchDTO.getCompetenceValues(), existingPerson);
            syncCompetenceValues(existingPerson, validatedCompetenceValues);
        }

        if (patchDTO.getPersonConflicts() != null) {
            List<PersonConflictEntity> validatedPersonConflicts = processPersonConflicts(patchDTO.getPersonConflicts(), existingPerson);
            syncPersonConflicts(existingPerson, validatedPersonConflicts);
        }

        return convertToResponseDTO(personRepository.save(existingPerson));
    }

    /**
     * Reporte de una persona: todos sus datos y los proyectos en los que ha
     * participado, con el rol desempeñado y la evaluación en cada uno.
     */
    @Transactional(readOnly = true)
    public PersonReportDTO getPersonReport(Long id) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PERSON_NOT_FOUND", id));

        PersonReportDTO dto = new PersonReportDTO();
        dto.setPerson(convertToResponseDTO(person));

        List<PersonReportDTO.ParticipationDTO> participations = new ArrayList<>();
        if (person.getRoleEvaluationList() != null) {
            for (RolePersonEvalEntity rpe : person.getRoleEvaluationList()) {
                CycleEntity cycle = rpe.getCycles();
                ProjectEntity project = cycle != null ? cycle.getProject() : null;
                participations.add(new PersonReportDTO.ParticipationDTO(
                        project != null ? modelMapper.map(project, ProjectDTO.ProjectSimpleDTO.class) : null,
                        rpe.getRoles() != null ? modelMapper.map(rpe.getRoles(), RoleDTO.RoleMinimalDTO.class) : null,
                        rpe.getRoleEvaluation() != null ? rpe.getRoleEvaluation().getSignificance() : null
                ));
            }
        }
        dto.setParticipations(participations);
        return dto;
    }

    // ========== MÉTODOS PRIVADOS ==========
    private void mapBasicFields(PersonEntity entity, PersonDTO.PersonCreateDTO dto, Long id) {
        entity.setId(id);
        entity.setAddress(dto.getAddress());
        entity.setBirthDate(dto.getBirthDate());
        entity.setEmail(dto.getEmail());
        entity.setExperience(dto.getExperience());
        entity.setCard(dto.getCard());
        entity.setInDate(dto.getInDate());
        entity.setPersonName(dto.getPersonName());
        entity.setPhone(dto.getPhone());
        entity.setSex(dto.getSex());
        entity.setSurName(dto.getSurName());
        if (dto.getStatus() != null)
            entity.setStatus(dto.getStatus());
    }

    private void processSimpleRelations(PersonDTO.PersonCreateDTO personDTO, PersonEntity person) {

        person.setGroup(personGroupRepository.findById(personDTO.getGroup())
                .orElseThrow(() -> new ResourceNotFoundException("ERR_PERSON_GROUP_NOT_FOUND", personDTO.getGroup())));

        if(personDTO.getCounty() != null){
            person.setCounty(countyRepository.findById(personDTO.getCounty())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_COUNTY_NOT_FOUND", personDTO.getCounty())));}

        if(personDTO.getRace() != null){
            person.setRace(raceRepository.findById(personDTO.getRace())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_RACE_NOT_FOUND", personDTO.getRace())));}

        if(personDTO.getNacionality() != null){
            person.setNacionality(nacionalityRepository.findById(personDTO.getNacionality())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_NATIONALITY_NOT_FOUND", personDTO.getNacionality())));}

        if(personDTO.getReligion() != null){
            person.setReligion(religionRepository.findById(personDTO.getReligion())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_RELIGION_NOT_FOUND", personDTO.getReligion())));}
    }

    private void processAgeGroup(PersonDTO.PersonCreateDTO personDTO, PersonEntity person) {
        if (personDTO.getBirthDate() != null) {
            int age = person.getAge(); // Usa el método getAge() de PersonEntity
            Optional<AgeGroupEntity> ageGroupOpt = ageGroupRepository.findAll().stream()
                    .filter(ag -> ag.getMinAge() <= age && ag.getMaxAge() >= age)
                    .findFirst();
            ageGroupOpt.ifPresent(person::setAgeGroup);
        }
    }

    private List<CompetenceValueEntity> processCompetenceValues(List<CompetenceValueDTO.CompetenceValueCreateDTO> competenceValuesDTO, PersonEntity person) {
        Set<Long> processedCompetenceIds = new HashSet<>();

        return competenceValuesDTO.stream().map(dto -> {
            if (!processedCompetenceIds.add(dto.getCompetenceId()))
                throw new BusinessRuleException("ERR_PERSON_DUPLICATE_COMPETENCE", dto.getCompetenceId());

            CompetenceEntity competence = competenceRepository.findById(dto.getCompetenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_COMPETENCE_NOT_FOUND", dto.getCompetenceId()));
            LevelsEntity level = levelsRepository.findById(dto.getLevelsId())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_LEVELS_NOT_FOUND", dto.getLevelsId()));

            CompetenceValueEntity cv = new CompetenceValueEntity();
            cv.setCompetence(competence);
            cv.setLevel(level);
            cv.setPerson(person);
            return cv;
        }).toList();
    }

    private void syncCompetenceValues(PersonEntity person, List<CompetenceValueEntity> validatedCompetenceValues) {
        Map<Long, CompetenceValueEntity> existingMap = person.getCompetenceValueList().stream()
                .collect(Collectors.toMap(cv -> cv.getCompetence().getId(), cv -> cv));
        List<CompetenceValueEntity> finalList = new ArrayList<>();

        for (CompetenceValueEntity validatedCv : validatedCompetenceValues) {
            Long competenceId = validatedCv.getCompetence().getId();
            if (existingMap.containsKey(competenceId)) {
                CompetenceValueEntity existing = existingMap.get(competenceId);
                existing.setLevel(validatedCv.getLevel());
                finalList.add(existing);
            } else {
                finalList.add(validatedCv);
            }
        }

        person.getCompetenceValueList().clear();
        person.getCompetenceValueList().addAll(finalList);
    }

    private List<PersonalInterestsEntity> processPersonalInterests(List<PersonalInterestDTO.PersonalInterestCreateDTO> personalInterestsDTO, PersonEntity person) {
        Set<Long> processedRoleIds = new HashSet<>();

        return personalInterestsDTO.stream().map(dto -> {
            if (!processedRoleIds.add(dto.getRoleId()))
                throw new BusinessRuleException("ERR_PERSON_DUPLICATE_ROLE", dto.getRoleId());

            RoleEntity role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_ROLE_NOT_FOUND", dto.getRoleId()));

            PersonalInterestsEntity pi = new PersonalInterestsEntity();
            pi.setRole(role);
            pi.setPreference(dto.getPreference());
            pi.setPerson(person);
            return pi;
        }).toList();
    }

    private void syncPersonalInterests(PersonEntity person, List<PersonalInterestsEntity> validatedPersonalInterests) {
        Map<Long, PersonalInterestsEntity> existingMap = person.getPersonalInterestsList().stream()
                .collect(Collectors.toMap(pi -> pi.getRole().getId(), pi -> pi));
        List<PersonalInterestsEntity> finalList = new ArrayList<>();

        for (PersonalInterestsEntity validatedPi : validatedPersonalInterests) {
            Long roleId = validatedPi.getRole().getId();
            if (existingMap.containsKey(roleId)) {
                PersonalInterestsEntity existing = existingMap.get(roleId);
                existing.setPreference(validatedPi.isPreference());
                finalList.add(existing);
            } else {
                finalList.add(validatedPi);
            }
        }
        person.getPersonalInterestsList().clear();
        person.getPersonalInterestsList().addAll(finalList);
    }

    private List<PersonalProjectInterestsEntity> processPersonalProjectInterests(List<PersonalProjectInterestDTO.PersonalProjectInterestCreateDTO> projectInterestsDTO, PersonEntity person) {
        Set<Long> processedProjectIds = new HashSet<>();

        return projectInterestsDTO.stream().map(dto -> {
            if (!processedProjectIds.add(dto.getProjectId()))
                throw new BusinessRuleException("ERR_PERSON_DUPLICATE_PROJECT", dto.getProjectId());

            ProjectEntity project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_PROJECT_NOT_FOUND", dto.getProjectId()));

            PersonalProjectInterestsEntity ppi = new PersonalProjectInterestsEntity();
            ppi.setProject(project);
            ppi.setPreference(dto.getPreference());
            ppi.setPerson(person);
            return ppi;
        }).toList();
    }

    private void syncPersonalProjectInterests(PersonEntity person, List<PersonalProjectInterestsEntity> validatedProjectInterests) {
        Map<Long, PersonalProjectInterestsEntity> existingMap = person.getPersonalProjectInterestsList().stream()
                .collect(Collectors.toMap(ppi -> ppi.getProject().getId(), ppi -> ppi));

        List<PersonalProjectInterestsEntity> finalList = new ArrayList<>();

        for (PersonalProjectInterestsEntity validatedPpi : validatedProjectInterests) {
            Long projectId = validatedPpi.getProject().getId();
            if (existingMap.containsKey(projectId)) {
                PersonalProjectInterestsEntity existing = existingMap.get(projectId);
                existing.setPreference(validatedPpi.isPreference());
                finalList.add(existing);
            } else {
                finalList.add(validatedPpi);
            }
        }
        person.getPersonalProjectInterestsList().clear();
        person.getPersonalProjectInterestsList().addAll(finalList);
    }

    private PersonTestEntity processPersonTest(PersonTestDTO.PersonTestCreateDTO personTestDTO, PersonEntity person) {
        PersonTestEntity pt = modelMapper.map(personTestDTO, PersonTestEntity.class);
        pt.setPerson(person);
        return pt;
    }

    private List<PersonConflictEntity> processPersonConflicts(List<PersonConflictDTO.PersonConflictCreateDTO> personConflictsDTO, PersonEntity person) {
        Set<Long> processedConflictKeys = new HashSet<>();

        return personConflictsDTO.stream().map(dto -> {
            if (!processedConflictKeys.add(dto.getPersonConflictId()))
                throw new BusinessRuleException("ERR_PERSON_DUPLICATE_CONFLICT");

            if (dto.getPersonConflictId().equals(person.getId()))
                throw new BusinessRuleException("ERR_PERSON_SELF_CONFLICT");

            ConflictIndexEntity conflictIndex = conflictIndexRepository.findById(dto.getConflictIndexId())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_CONFLICT_INDEX_NOT_FOUND", dto.getConflictIndexId()));
            PersonEntity otherPerson = personRepository.findById(dto.getPersonConflictId())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_PERSON_NOT_FOUND", dto.getPersonConflictId()));

            PersonConflictEntity pc = new PersonConflictEntity();
            pc.setIndex(conflictIndex);
            pc.setPersonConflict(otherPerson);
            pc.setPerson(person);
            return pc;
        }).toList();
    }

    private void syncPersonConflicts(PersonEntity person, List<PersonConflictEntity> validatedPersonConflicts) {
        Map<String, PersonConflictEntity> existingMap = person.getPersonConflictList().stream()
                .collect(Collectors.toMap(
                        pc -> generateConflictKey(pc.getPersonConflict().getId(), pc.getIndex().getId()),
                        pc -> pc
                ));

        List<PersonConflictEntity> finalList = new ArrayList<>();

        for (PersonConflictEntity validatedPc : validatedPersonConflicts) {
            String key = generateConflictKey(validatedPc.getPersonConflict().getId(), validatedPc.getIndex().getId());
            finalList.add(existingMap.getOrDefault(key, validatedPc));
        }
        person.getPersonConflictList().clear();
        person.getPersonConflictList().addAll(finalList);
    }

    private String generateConflictKey(Long personConflictId, Long conflictIndexId) {
        return personConflictId + "-" + conflictIndexId;
    }

    private PersonDTO.PersonResponseDTO convertToResponseDTO(PersonEntity person) {
        PersonDTO.PersonResponseDTO responseDTO = new PersonDTO.PersonResponseDTO();
        responseDTO.setId(person.getId());
        responseDTO.setAddress(person.getAddress());
        responseDTO.setBirthDate(person.getBirthDate());
        responseDTO.setEmail(person.getEmail());
        responseDTO.setExperience(person.getExperience());
        responseDTO.setCard(person.getCard());
        responseDTO.setInDate(person.getInDate());
        responseDTO.setPersonName(person.getPersonName());
        responseDTO.setPhone(person.getPhone());
        responseDTO.setSex(person.getSex());
        responseDTO.setStatus(person.getStatus().toString());
        responseDTO.setSurName(person.getSurName());
        responseDTO.setWorkload(person.getWorkload());

        responseDTO.setAge(person.getAge());

        // Relaciones opcionales (pueden ser null, p. ej. en personas importadas):
        // ModelMapper lanza "source cannot be null" si se le pasa null, por eso se protegen.
        if (person.getCounty() != null) {
            responseDTO.setCounty(modelMapper.map(person.getCounty(), CountyDTO.CountyResponseDTO.class));
        }
        if (person.getRace() != null) {
            responseDTO.setRace(modelMapper.map(person.getRace(), RaceDTO.RaceResponseDTO.class));
        }
        if (person.getGroup() != null) {
            responseDTO.setGroup(modelMapper.map(person.getGroup(), PersonGroupDTO.PersonGroupResponseDTO.class));
        }
        if (person.getNacionality() != null) {
            responseDTO.setNacionality(modelMapper.map(person.getNacionality(), NationalityDTO.NacionalityResponseDTO.class));
        }
        if (person.getReligion() != null) {
            responseDTO.setReligion(modelMapper.map(person.getReligion(), ReligionDTO.ReligionResponseDTO.class));
        }
        if (person.getAgeGroup() != null) {
            responseDTO.setAgeGroup(modelMapper.map(person.getAgeGroup(), AgeGroupDTO.AgeGroupResponseDTO.class));
        }

        if (person.getCompetenceValueList() != null) {
            responseDTO.setCompetenceValues(person.getCompetenceValueList().stream()
                    .map(cv -> {
                        CompetenceValueDTO.CompetenceValueResponseDTO dto = modelMapper.map(cv, CompetenceValueDTO.CompetenceValueResponseDTO.class);
                        dto.setCompetence(modelMapper.map(cv.getCompetence(), CompetenceDTO.CompetenceMinimalDTO.class));
                        dto.setLevel(modelMapper.map(cv.getLevel(), LevelsDTO.LevelsResponseDTO.class));
                        return dto;
                    })
                    .toList());
        }

        if (person.getPersonalInterestsList() != null) {
            responseDTO.setPersonalInterests(person.getPersonalInterestsList().stream()
                    .map(pi -> {
                        PersonalInterestDTO.PersonalInterestResponseDTO dto = modelMapper.map(pi, PersonalInterestDTO.PersonalInterestResponseDTO.class);
                        dto.setRole(modelMapper.map(pi.getRole(), RoleDTO.RoleMinimalDTO.class));
                        return dto;
                    })
                    .toList());
        }

        if (person.getPersonalProjectInterestsList() != null) {
            responseDTO.setPersonalProjectInterests(person.getPersonalProjectInterestsList().stream()
                    .map(ppi -> {
                        PersonalProjectInterestDTO.PersonalProjectInterestResponseDTO dto = modelMapper.map(ppi, PersonalProjectInterestDTO.PersonalProjectInterestResponseDTO.class);
                        dto.setProject(modelMapper.map(ppi.getProject(), ProjectDTO.ProjectResponseDTO.class));
                        return dto;
                    })
                    .toList());
        }

        if (person.getPersonTest() != null) {
            responseDTO.setPersonTest(modelMapper.map(person.getPersonTest(), PersonTestDTO.PersonTestResponseDTO.class));
        }

        if (person.getPersonConflictList() != null) {
            responseDTO.setPersonConflicts(person.getPersonConflictList().stream()
                    .map(pc -> {
                        PersonConflictDTO.PersonConflictResponseDTO dto = modelMapper.map(pc, PersonConflictDTO.PersonConflictResponseDTO.class);
                        dto.setConflictIndex(modelMapper.map(pc.getIndex(), ConflictIndexDTO.ConflictIndexResponseDTO.class));
                        dto.setPersonConflict(modelMapper.map(pc.getPersonConflict(), PersonDTO.PersonMinimalDTO.class));
                        return dto;
                    })
                    .toList());
        }

        return responseDTO;
    }
}