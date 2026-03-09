package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.entity.auxiliar.Status;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.presentation.dto.*;
import com.tesis.teamsoft.service.interfaces.IPersonService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements IPersonService {

    @Autowired private IPersonRepository personRepository;
    @Autowired private ICountyRepository countyRepository;
    @Autowired private IRaceRepository raceRepository;
    @Autowired private IPersonGroupRepository personGroupRepository;
    @Autowired private INacionalityRepository nacionalityRepository;
    @Autowired private IReligionRepository religionRepository;
    @Autowired private IAgeGroupRepository ageGroupRepository;
    @Autowired private ICompetenceRepository competenceRepository;
    @Autowired private ILevelsRepository levelsRepository;
    @Autowired private IRoleRepository roleRepository;
    @Autowired private IProjectRepository projectRepository;
    @Autowired private IConflictIndexRepository conflictIndexRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public PersonDTO.PersonResponseDTO savePerson(PersonDTO.PersonCreateDTO personDTO) {
        try {
            PersonEntity person = modelMapper.map(personDTO, PersonEntity.class);
            person.setId(null);
            person.setStatus(Status.ACTIVE);

            // Procesar relaciones simples
            processSimpleRelations(personDTO, person);

            // Procesar AgeGroup (automático si no se proporciona)
            processAgeGroup(personDTO, person);

            // Procesar listas
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

            person = personRepository.save(person);

            return convertToResponseDTO(person);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error saving person: Data integrity violation");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error saving person: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PersonDTO.PersonResponseDTO updatePerson(PersonDTO.PersonCreateDTO personDTO, Long id) {
        PersonEntity existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with ID: " + id));

        try {
            existingPerson.setId(id);
            existingPerson.setAddress(personDTO.getAddress());
            existingPerson.setBirthDate(personDTO.getBirthDate());
            existingPerson.setEmail(personDTO.getEmail());
            existingPerson.setExperience(personDTO.getExperience());
            existingPerson.setCard(personDTO.getCard());
            existingPerson.setInDate(personDTO.getInDate());
            existingPerson.setPersonName(personDTO.getPersonName());
            existingPerson.setPhone(personDTO.getPhone());
            existingPerson.setSex(personDTO.getSex());
            existingPerson.setSurName(personDTO.getSurName());

            // Procesar relaciones simples
            processSimpleRelations(personDTO, existingPerson);

            // Procesar AgeGroup
            processAgeGroup(personDTO, existingPerson);

            // Sincronizar listas
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
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error updating person: Data integrity violation");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating person: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String deletePerson(Long id) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with ID: " + id));

        // Verificar si tiene relaciones antes de eliminar
        if (!person.getAssignedRoleList().isEmpty() ||
                !person.getRoleEvaluationList().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete person because it has associated relations");
        }

        personRepository.deleteById(id);
        return "Person deleted successfully";
    }

    @Override
    public List<PersonDTO.PersonResponseDTO> findAllPerson() {
        try {
            return personRepository.findAll().stream()
                    .map(this::convertToResponseDTO)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding all persons: " + e.getMessage());
        }
    }

    @Override
    public List<PersonDTO.PersonResponseDTO> findAllByOrderByIdAsc() {
        try {
            return personRepository.findAllByOrderByIdAsc().stream()
                    .map(this::convertToResponseDTO)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding all persons: " + e.getMessage());
        }
    }

    @Override
    public PersonDTO.PersonResponseDTO findPersonById(Long id) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with ID: " + id));
        return convertToResponseDTO(person);
    }

    // ========== MÉTODOS PRIVADOS ==========

    private void processSimpleRelations(PersonDTO.PersonCreateDTO personDTO, PersonEntity person) {
        person.setCounty(countyRepository.findById(personDTO.getCounty())
                .orElseThrow(() -> new RuntimeException("County not found with ID: " + personDTO.getCounty())));
        person.setRace(raceRepository.findById(personDTO.getRace())
                .orElseThrow(() -> new RuntimeException("Race not found with ID: " + personDTO.getRace())));
        person.setGroup(personGroupRepository.findById(personDTO.getGroup())
                .orElseThrow(() -> new RuntimeException("Person group not found with ID: " + personDTO.getGroup())));
        person.setNacionality(nacionalityRepository.findById(personDTO.getNacionality())
                .orElseThrow(() -> new RuntimeException("Nacionality not found with ID: " + personDTO.getNacionality())));
        person.setReligion(religionRepository.findById(personDTO.getReligion())
                .orElseThrow(() -> new RuntimeException("Religion not found with ID: " + personDTO.getReligion())));
    }

    private void processAgeGroup(PersonDTO.PersonCreateDTO personDTO, PersonEntity person) {
       if (personDTO.getBirthDate() != null) {
            // Calcular AgeGroup automáticamente basado en la edad
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
            if (!processedCompetenceIds.add(dto.getCompetenceId())) {
                throw new IllegalArgumentException("Duplicate competence ID: " + dto.getCompetenceId());
            }

            CompetenceEntity competence = competenceRepository.findById(dto.getCompetenceId())
                    .orElseThrow(() -> new RuntimeException("Competence not found with ID: " + dto.getCompetenceId()));
            LevelsEntity level = levelsRepository.findById(dto.getLevelsId())
                    .orElseThrow(() -> new RuntimeException("Levels not found with ID: " + dto.getLevelsId()));

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
            if (!processedRoleIds.add(dto.getRoleId())) {
                throw new IllegalArgumentException("Duplicate role ID: " + dto.getRoleId());
            }

            RoleEntity role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + dto.getRoleId()));

            PersonalInterestsEntity pi = new PersonalInterestsEntity();
            pi.setRole(role);
            pi.setPreference(dto.getPreference());
            pi.setPerson(person);
            return pi;
        }).collect(Collectors.toList());
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

        List<ProjectEntity> allProjects = projectRepository.findAll();
        Map<Long, ProjectEntity> projectMap = allProjects.stream()
                .collect(Collectors.toMap(ProjectEntity::getId, Function.identity()));

        return projectInterestsDTO.stream().map(dto -> {
            if (!processedProjectIds.add(dto.getProjectId())) {
                throw new IllegalArgumentException("Duplicate project ID: " + dto.getProjectId());
            }

            ProjectEntity project = projectMap.get(dto.getProjectId());
            if (project == null) {
                throw new RuntimeException("Project not found with ID: " + dto.getProjectId());
            }

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
            if (!processedConflictKeys.add(dto.getPersonConflictId())) {
                throw new IllegalArgumentException("Duplicate person conflict: person " + dto.getPersonConflictId() + " with index " + dto.getConflictIndexId());
            }

            // Validar que no sea la misma persona
            if (dto.getPersonConflictId().equals(person.getId())) {
                throw new IllegalArgumentException("Person cannot have conflict with itself");
            }

            ConflictIndexEntity conflictIndex = conflictIndexRepository.findById(dto.getConflictIndexId())
                    .orElseThrow(() -> new RuntimeException("Conflict index not found with ID: " + dto.getConflictIndexId()));
            PersonEntity otherPerson = personRepository.findById(dto.getPersonConflictId())
                    .orElseThrow(() -> new RuntimeException("Person conflict not found with ID: " + dto.getPersonConflictId()));

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
            // Mantener existente (no hay campos para actualizar)
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

        // Calcular edad
        responseDTO.setAge(person.getAge());

        // Convertir relaciones simples
        responseDTO.setCounty(modelMapper.map(person.getCounty(), CountyDTO.CountyResponseDTO.class));
        responseDTO.setRace(modelMapper.map(person.getRace(), RaceDTO.RaceResponseDTO.class));
        responseDTO.setGroup(modelMapper.map(person.getGroup(), PersonGroupDTO.PersonGroupResponseDTO.class));
        responseDTO.setNacionality(modelMapper.map(person.getNacionality(), NationalityDTO.NacionalityResponseDTO.class));
        responseDTO.setReligion(modelMapper.map(person.getReligion(), ReligionDTO.ReligionResponseDTO.class));
        if (person.getAgeGroup() != null) {
            responseDTO.setAgeGroup(modelMapper.map(person.getAgeGroup(), AgeGroupDTO.AgeGroupResponseDTO.class));
        }

        // Convertir listas
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