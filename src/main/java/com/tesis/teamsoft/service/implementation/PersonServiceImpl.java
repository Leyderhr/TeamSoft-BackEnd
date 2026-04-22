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
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + id));

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
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + id));

        if((person.getAssignedRoleList() != null && !person.getAssignedRoleList().isEmpty()) ||
                (person.getRoleEvaluationList() != null && !person.getRoleEvaluationList().isEmpty()))
            throw new BusinessRuleException("Cannot delete person because it has associated relations");

        personRepository.deleteById(id);
        return "Person deleted successfully";
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
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + id));
        return convertToResponseDTO(person);
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
    }

    private void processSimpleRelations(PersonDTO.PersonCreateDTO personDTO, PersonEntity person) {
        person.setCounty(countyRepository.findById(personDTO.getCounty())
                .orElseThrow(() -> new ResourceNotFoundException("County not found with ID: " + personDTO.getCounty())));
        person.setRace(raceRepository.findById(personDTO.getRace())
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with ID: " + personDTO.getRace())));
        person.setGroup(personGroupRepository.findById(personDTO.getGroup())
                .orElseThrow(() -> new ResourceNotFoundException("Person group not found with ID: " + personDTO.getGroup())));
        person.setNacionality(nacionalityRepository.findById(personDTO.getNacionality())
                .orElseThrow(() -> new ResourceNotFoundException("Nacionality not found with ID: " + personDTO.getNacionality())));
        person.setReligion(religionRepository.findById(personDTO.getReligion())
                .orElseThrow(() -> new ResourceNotFoundException("Religion not found with ID: " + personDTO.getReligion())));
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
                throw new BusinessRuleException("Duplicate competence ID: " + dto.getCompetenceId());

            CompetenceEntity competence = competenceRepository.findById(dto.getCompetenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Competence not found with ID: " + dto.getCompetenceId()));
            LevelsEntity level = levelsRepository.findById(dto.getLevelsId())
                    .orElseThrow(() -> new ResourceNotFoundException("Levels not found with ID: " + dto.getLevelsId()));

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
                throw new BusinessRuleException("Duplicate role ID: " + dto.getRoleId());

            RoleEntity role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + dto.getRoleId()));

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
                throw new BusinessRuleException("Duplicate project ID: " + dto.getProjectId());

            ProjectEntity project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + dto.getProjectId()));

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
                throw new BusinessRuleException("Duplicate person conflict: person " + dto.getPersonConflictId() + " with index " + dto.getConflictIndexId());

            if (dto.getPersonConflictId().equals(person.getId()))
                throw new BusinessRuleException("Person cannot have conflict with itself");

            ConflictIndexEntity conflictIndex = conflictIndexRepository.findById(dto.getConflictIndexId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conflict index not found with ID: " + dto.getConflictIndexId()));
            PersonEntity otherPerson = personRepository.findById(dto.getPersonConflictId())
                    .orElseThrow(() -> new ResourceNotFoundException("Person conflict not found with ID: " + dto.getPersonConflictId()));

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

        responseDTO.setCounty(modelMapper.map(person.getCounty(), CountyDTO.CountyResponseDTO.class));
        responseDTO.setRace(modelMapper.map(person.getRace(), RaceDTO.RaceResponseDTO.class));
        responseDTO.setGroup(modelMapper.map(person.getGroup(), PersonGroupDTO.PersonGroupResponseDTO.class));
        responseDTO.setNacionality(modelMapper.map(person.getNacionality(), NationalityDTO.NacionalityResponseDTO.class));
        responseDTO.setReligion(modelMapper.map(person.getReligion(), ReligionDTO.ReligionResponseDTO.class));
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