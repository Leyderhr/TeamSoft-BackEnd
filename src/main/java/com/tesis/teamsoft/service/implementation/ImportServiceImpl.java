package com.tesis.teamsoft.service.implementation;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import com.tesis.teamsoft.persistence.entity.*;
import com.tesis.teamsoft.persistence.entity.auxiliary.Status;
import com.tesis.teamsoft.persistence.repository.*;
import com.tesis.teamsoft.presentation.dto.ImportDTO;
import com.tesis.teamsoft.presentation.dto.ImportResultDTO;
import com.tesis.teamsoft.service.interfaces.IImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Migración de la lógica de importación CSV del backend JSF legado
 * (ImportarController, I_StepReadFileController, I_StepSelectPersonController)
 * a un servicio Spring stateless.
 */
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements IImportService {

    private static final String TYPE_NUMBER = "number";
    private static final String TYPE_TEXT = "text";
    private static final String[] MBTI_TYPES = {
            "ESTJ", "ENTJ", "ESFJ", "ENFJ", "ESTP", "ENTP", "ESFP", "ENFP",
            "ISTJ", "INTJ", "ISFJ", "INFJ", "ISTP", "INTP", "ISFP", "INFP"
    };

    private final ImportFileStorage fileStorage;
    private final IPersonRepository personRepository;
    private final IPersonGroupRepository personGroupRepository;
    private final ICompetenceRepository competenceRepository;
    private final ILevelsRepository levelsRepository;
    private final IRoleRepository roleRepository;
    private final ICountyRepository countyRepository;
    private final ICompetenceValueRepository competenceValueRepository;
    private final IRoleExperienceRepository roleExperienceRepository;
    private final IPersonalInterestsRepository personalInterestsRepository;
    private final IPersonTestRepository personTestRepository;

    // ───────────────────────── PASO 1: parse ─────────────────────────
    @Override
    public ImportDTO.ParseResponseDTO parse(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("ERR_IMPORT_EMPTY_FILE");
        }
        byte[] content = readBytes(file);
        List<String[]> rows = readAllRows(content);
        if (rows.isEmpty()) {
            throw new BusinessRuleException("ERR_IMPORT_EMPTY_FILE");
        }

        List<String> headers = sanitizeHeaders(rows.get(0));
        List<String[]> data = rows.subList(1, rows.size());
        if (data.isEmpty()) {
            throw new BusinessRuleException("ERR_IMPORT_NO_DATA");
        }

        checkHomogeneousData(headers, data);

        String fileId = fileStorage.store(content);

        ImportDTO.ParseResponseDTO response = new ImportDTO.ParseResponseDTO();
        response.setFileId(fileId);
        response.setHeaders(headers);
        response.setPreview(buildPreview(headers, data));
        return response;
    }

    /** Réplica de checkHomogeneousData(): el 1er dato define el tipo; todas las filas deben coincidir. */
    private void checkHomogeneousData(List<String> headers, List<String[]> data) {
        List<String> badColumns = new ArrayList<>();
        for (int col = 0; col < headers.size(); col++) {
            String expectedType = classOf(cell(data.get(0), col));
            for (int row = 1; row < data.size(); row++) {
                if (!classOf(cell(data.get(row), col)).equals(expectedType)) {
                    badColumns.add(headers.get(col));
                    break;
                }
            }
        }
        if (!badColumns.isEmpty()) {
            throw new BusinessRuleException("ERR_IMPORT_NOT_HOMOGENEOUS", String.join(", ", badColumns));
        }
    }

    // ───────────────────────── PASO 2: execute ─────────────────────────
    @Override
    @Transactional
    public ImportResultDTO execute(ImportDTO.ExecuteRequestDTO req) {
        byte[] content = fileStorage.read(req.getFileId());
        try {
            List<String[]> rows = readAllRows(content);
            if (rows.size() < 2) {
                throw new BusinessRuleException("ERR_IMPORT_NO_DATA");
            }
            List<String> headers = sanitizeHeaders(rows.get(0));
            List<String[]> data = rows.subList(1, rows.size());
            Map<String, Integer> index = buildIndex(headers);

            validateColumns(req, index);

            Map<String, CompetenceEntity> competences = resolveCompetences(req);
            Map<Long, RoleEntity> roles = resolveRoles(req);
            List<LevelsEntity> levels = levelsRepository.findAllByOrderByLevelsAsc();
            if (levels.isEmpty()) {
                throw new BusinessRuleException("ERR_IMPORT_NO_LEVELS");
            }
            Map<String, Double> columnMax = computeColumnMaxes(req, data, index);
            List<CountyEntity> counties = countyRepository.findAll();
            PersonGroupEntity group = findOrCreateGroup(req.getGroupName());

            ImportResultDTO result = new ImportResultDTO();
            Set<String> usedEmails = new HashSet<>();
            Set<String> usedCards = new HashSet<>();

            for (String[] row : data) {
                String name = cell(row, index.get(req.getPersonMapping().getNombreColumn())).trim();
                try {
                    processRow(req, row, index, group, competences, roles, levels,
                            columnMax, counties, usedEmails, usedCards, result);
                } catch (Exception e) {
                    result.setErrors(result.getErrors() + 1);
                    result.getErrorMessages().add(name + ": " + e.getMessage());
                }
            }
            return result;
        } finally {
            fileStorage.delete(req.getFileId());
        }
    }

    private void processRow(ImportDTO.ExecuteRequestDTO req, String[] row, Map<String, Integer> index,
                            PersonGroupEntity group, Map<String, CompetenceEntity> competences,
                            Map<Long, RoleEntity> roles, List<LevelsEntity> levels,
                            Map<String, Double> columnMax, List<CountyEntity> counties,
                            Set<String> usedEmails, Set<String> usedCards, ImportResultDTO result) {

        String name = cell(row, index.get(req.getPersonMapping().getNombreColumn())).trim();
        if (name.isEmpty()) {
            throw new BusinessRuleException("ERR_IMPORT_ROW_NO_NAME");
        }
        int experience = parseIntStrict(cell(row, index.get(req.getPersonMapping().getExperienceColumn())));

        Optional<PersonEntity> existing =
                personRepository.findFirstByPersonNameAndExperienceAndGroup_Id(name, experience, group.getId());

        PersonEntity person;
        boolean isUpdate;
        if (existing.isPresent()) {
            if (!req.isUpdateIfExist()) {
                result.setSkipped(result.getSkipped() + 1);
                return;
            }
            person = existing.get();
            person.setPersonName(name);
            person.setExperience(experience);
            person = personRepository.save(person);
            isUpdate = true;
        } else {
            person = personRepository.save(buildNewPerson(name, experience, group, counties, usedEmails, usedCards));
            ensurePersonTest(person);
            isUpdate = false;
        }

        processCompetences(person, row, index, req.getCompetenceMapping(), competences, columnMax, levels);
        processRoles(person, row, index, req, roles);

        if (isUpdate) {
            result.setUpdated(result.getUpdated() + 1);
        } else {
            result.setCreated(result.getCreated() + 1);
        }
    }

    // ───────────────────────── Persona (réplica de setPersonData) ─────────────────────────
    private PersonEntity buildNewPerson(String name, int experience, PersonGroupEntity group,
                                        List<CountyEntity> counties, Set<String> usedEmails, Set<String> usedCards) {
        PersonEntity p = new PersonEntity();
        p.setPersonName(name);
        p.setSurName("");
        p.setAddress("");
        p.setExperience(experience);
        p.setInDate(new Date());
        p.setStatus(Status.ACTIVE);
        p.setWorkload(0f);
        char sex = randomSex();
        p.setSex(sex);
        p.setEmail(uniqueEmail(name, usedEmails));
        p.setPhone(randomPhone());
        p.setCard(uniqueCard(sex, usedCards));
        if (!counties.isEmpty()) {
            p.setCounty(counties.get(ThreadLocalRandom.current().nextInt(counties.size())));
        }
        p.setGroup(group);
        return p;
    }

    /** Inicializa el WorkerTest con Belbin 'I' y un MBTI aleatorio (réplica del legado). */
    private void ensurePersonTest(PersonEntity person) {
        if (personTestRepository.findByPerson_Id(person.getId()).isPresent()) {
            return;
        }
        PersonTestEntity test = new PersonTestEntity();
        test.setES('I');
        test.setIM('I');
        test.setCO('I');
        test.setIS('I');
        test.setCE('I');
        test.setIR('I');
        test.setME('I');
        test.setCH('I');
        test.setIF('I');
        test.setMbtiType(MBTI_TYPES[ThreadLocalRandom.current().nextInt(MBTI_TYPES.length)]);
        test.setPerson(person);
        personTestRepository.save(test);
    }

    // ───────────────────────── Competencias (réplica de getCompetencesValue) ─────────────────────────
    private void processCompetences(PersonEntity person, String[] row, Map<String, Integer> index,
                                    List<ImportDTO.CompetenceMappingDTO> mappings,
                                    Map<String, CompetenceEntity> competences,
                                    Map<String, Double> columnMax, List<LevelsEntity> levels) {
        if (mappings == null) return;
        for (ImportDTO.CompetenceMappingDTO cm : mappings) {
            CompetenceEntity competence = competences.get(cm.getCompetenceName());
            float value = computeCompetenceValue(cm, row, index, columnMax);
            LevelsEntity level = getLevelByValue(value, levels);
            upsertCompetenceValue(person, competence, level);
        }
    }

    private float computeCompetenceValue(ImportDTO.CompetenceMappingDTO cm, String[] row,
                                         Map<String, Integer> index, Map<String, Double> columnMax) {
        List<ImportDTO.AttributeDTO> attrs = cm.getAttributes();
        if (attrs == null || attrs.isEmpty()) return 0f;

        if (attrs.size() > 1) {
            float total = 0f;
            for (ImportDTO.AttributeDTO attr : attrs) {
                total += attributeScore(attr, row, index, columnMax) * attr.getWeight();
            }
            return clamp01(total);
        }
        // Caso de un solo atributo: NO se multiplica por el peso (réplica exacta del legado)
        return clamp01(attributeScore(attrs.get(0), row, index, columnMax));
    }

    /** Score base de un atributo en [0,1]: numérico = valor/máxColumna; texto = peso del valor mapeado. */
    private float attributeScore(ImportDTO.AttributeDTO attr, String[] row,
                                 Map<String, Integer> index, Map<String, Double> columnMax) {
        String raw = cell(row, index.get(attr.getCsvColumn()));
        if (attr.isNumeric()) {
            double max = columnMax.getOrDefault(attr.getCsvColumn(), 0.0);
            if (max <= 0) return 0f;
            return (float) (parseDoubleSafe(raw) / max);
        }
        Map<String, Float> weights = attr.getTextValueWeights();
        if (weights == null) return 0f;
        return weights.getOrDefault(raw.trim(), 0f);
    }

    /** Réplica de getLevelByValue(): divide [0,1] en n porciones iguales y ubica el valor. */
    private LevelsEntity getLevelByValue(float value, List<LevelsEntity> levels) {
        int n = levels.size();
        float portion = 1f / n;
        float accumulated = portion;
        int pos = 0;
        while (value > accumulated && pos < n - 1) {
            accumulated += portion;
            pos++;
        }
        return levels.get(pos);
    }

    private void upsertCompetenceValue(PersonEntity person, CompetenceEntity competence, LevelsEntity level) {
        CompetenceValueEntity cv = competenceValueRepository
                .findByPerson_IdAndCompetence_Id(person.getId(), competence.getId())
                .orElseGet(CompetenceValueEntity::new);
        cv.setPerson(person);
        cv.setCompetence(competence);
        cv.setLevel(level);
        competenceValueRepository.save(cv);
    }

    // ───────────────────────── Roles (réplica de setRoleData) ─────────────────────────
    private void processRoles(PersonEntity person, String[] row, Map<String, Integer> index,
                              ImportDTO.ExecuteRequestDTO req, Map<Long, RoleEntity> roles) {
        if (req.getRoleMapping() == null) return;

        if (req.isDeleteOldValues()) {
            roleExperienceRepository.deleteByPerson_Id(person.getId());
            personalInterestsRepository.deleteByPerson_Id(person.getId());
        }

        int maxExp = req.getMaxExpValue();
        float workerExp = (maxExp <= 0 || person.getExperience() >= maxExp)
                ? 1f : (float) person.getExperience() / maxExp;

        for (ImportDTO.RoleMappingDTO rm : req.getRoleMapping()) {
            RoleEntity role = roles.get(rm.getRoleId());
            int yearsInRole = parseIntStrict(cell(row, index.get(rm.getCsvColumn())));
            float roleRatio = person.getExperience() <= 0 ? 0f : (float) yearsInRole / person.getExperience();

            upsertRoleExperience(person, role, workerExp, req.isDeleteOldValues());
            upsertPersonalInterest(person, role, roleRatio >= req.getPuntoCorte(), req.isDeleteOldValues());
        }
    }

    private void upsertRoleExperience(PersonEntity person, RoleEntity role, float indexes, boolean deletedOld) {
        RoleExperienceEntity re = deletedOld ? null
                : roleExperienceRepository.findByPerson_IdAndRole_Id(person.getId(), role.getId()).orElse(null);
        if (re == null) {
            re = new RoleExperienceEntity();
            re.setPerson(person);
            re.setRole(role);
        }
        re.setIndexes(indexes);
        roleExperienceRepository.save(re);
    }

    private void upsertPersonalInterest(PersonEntity person, RoleEntity role, boolean preference, boolean deletedOld) {
        PersonalInterestsEntity pi = deletedOld ? null
                : personalInterestsRepository.findByPerson_IdAndRole_Id(person.getId(), role.getId()).orElse(null);
        if (pi == null) {
            pi = new PersonalInterestsEntity();
            pi.setPerson(person);
            pi.setRole(role);
        }
        pi.setPreference(preference);
        personalInterestsRepository.save(pi);
    }

    private PersonGroupEntity findOrCreateGroup(String groupName) {
        return personGroupRepository.findByName(groupName).orElseGet(() -> {
            PersonGroupEntity group = new PersonGroupEntity();
            group.setName(groupName);
            return personGroupRepository.save(group);
        });
    }

    // ───────────────────────── Validación / resolución de mapeos ─────────────────────────
    private void validateColumns(ImportDTO.ExecuteRequestDTO req, Map<String, Integer> index) {
        requireColumn(req.getPersonMapping().getNombreColumn(), index);
        requireColumn(req.getPersonMapping().getExperienceColumn(), index);
        if (req.getCompetenceMapping() != null) {
            req.getCompetenceMapping().forEach(cm -> {
                if (cm.getAttributes() != null) {
                    cm.getAttributes().forEach(a -> requireColumn(a.getCsvColumn(), index));
                }
            });
        }
        if (req.getRoleMapping() != null) {
            req.getRoleMapping().forEach(rm -> requireColumn(rm.getCsvColumn(), index));
        }
    }

    private void requireColumn(String column, Map<String, Integer> index) {
        if (!index.containsKey(column)) {
            throw new BusinessRuleException("ERR_IMPORT_COLUMN_NOT_FOUND", column);
        }
    }

    private Map<String, CompetenceEntity> resolveCompetences(ImportDTO.ExecuteRequestDTO req) {
        Map<String, CompetenceEntity> map = new HashMap<>();
        if (req.getCompetenceMapping() == null) return map;
        for (ImportDTO.CompetenceMappingDTO cm : req.getCompetenceMapping()) {
            CompetenceEntity competence = competenceRepository.findByCompetitionName(cm.getCompetenceName())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_COMPETENCE_NOT_FOUND", cm.getCompetenceName()));
            map.put(cm.getCompetenceName(), competence);
        }
        return map;
    }

    private Map<Long, RoleEntity> resolveRoles(ImportDTO.ExecuteRequestDTO req) {
        Map<Long, RoleEntity> map = new HashMap<>();
        if (req.getRoleMapping() == null) return map;
        for (ImportDTO.RoleMappingDTO rm : req.getRoleMapping()) {
            RoleEntity role = roleRepository.findById(rm.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("ERR_ROLE_NOT_FOUND", rm.getRoleId()));
            map.put(rm.getRoleId(), role);
        }
        return map;
    }

    /** Máximo numérico por columna sobre todo el CSV (réplica del cálculo dinámico del legado). */
    private Map<String, Double> computeColumnMaxes(ImportDTO.ExecuteRequestDTO req, List<String[]> data,
                                                   Map<String, Integer> index) {
        Map<String, Double> maxes = new HashMap<>();
        if (req.getCompetenceMapping() == null) return maxes;
        for (ImportDTO.CompetenceMappingDTO cm : req.getCompetenceMapping()) {
            if (cm.getAttributes() == null) continue;
            for (ImportDTO.AttributeDTO attr : cm.getAttributes()) {
                if (!attr.isNumeric() || maxes.containsKey(attr.getCsvColumn())) continue;
                int col = index.get(attr.getCsvColumn());
                double max = 0.0;
                for (String[] row : data) {
                    String raw = cell(row, col).trim();
                    if (raw.isEmpty()) continue;
                    try {
                        max = Math.max(max, Double.parseDouble(raw));
                    } catch (NumberFormatException ignored) {
                        // celdas no numéricas se ignoran para el máximo
                    }
                }
                maxes.put(attr.getCsvColumn(), max);
            }
        }
        return maxes;
    }

    // ───────────────────────── Helpers CSV ─────────────────────────
    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new BusinessRuleException("ERR_IMPORT_PARSE_FAILED");
        }
    }

    private List<String[]> readAllRows(byte[] content) {
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new ByteArrayInputStream(content), StandardCharsets.UTF_8))) {
            return reader.readAll();
        } catch (IOException | CsvException e) {
            throw new BusinessRuleException("ERR_IMPORT_PARSE_FAILED");
        }
    }

    private List<String> sanitizeHeaders(String[] headerRow) {
        List<String> headers = new ArrayList<>(headerRow.length);
        for (int i = 0; i < headerRow.length; i++) {
            String h = headerRow[i] == null ? "" : headerRow[i];
            if (i == 0 && !h.isEmpty() && h.charAt(0) == '﻿') {
                h = h.substring(1); // quitar BOM del primer encabezado
            }
            headers.add(h.trim());
        }
        return headers;
    }

    private Map<String, Integer> buildIndex(List<String> headers) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            index.put(headers.get(i), i);
        }
        return index;
    }

    private List<Map<String, String>> buildPreview(List<String> headers, List<String[]> data) {
        List<Map<String, String>> preview = new ArrayList<>();
        int limit = Math.min(5, data.size());
        for (int r = 0; r < limit; r++) {
            Map<String, String> rowMap = new LinkedHashMap<>();
            for (int c = 0; c < headers.size(); c++) {
                rowMap.put(headers.get(c), cell(data.get(r), c));
            }
            preview.add(rowMap);
        }
        return preview;
    }

    private String cell(String[] row, Integer idx) {
        if (idx == null || idx < 0 || idx >= row.length) return "";
        return row[idx] == null ? "" : row[idx];
    }

    private String classOf(String value) {
        return isInteger(value) ? TYPE_NUMBER : TYPE_TEXT;
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int parseIntStrict(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) return 0;
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new BusinessRuleException("ERR_IMPORT_INVALID_NUMBER", trimmed);
        }
    }

    private double parseDoubleSafe(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private float clamp01(float value) {
        if (value < 0f) return 0f;
        return Math.min(value, 1f);
    }

    // ───────────────────────── Generadores (réplica de I_StepUtil) ─────────────────────────
    private char randomSex() {
        return ThreadLocalRandom.current().nextBoolean() ? 'F' : 'M';
    }

    private String randomPhone() {
        StringBuilder sb = new StringBuilder("5");
        for (int i = 0; i < 7; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }

    private String uniqueEmail(String name, Set<String> used) {
        String base = name.toLowerCase().replaceAll("\\s+", "");
        if (base.isEmpty()) base = "persona";
        String candidate = base + "@ceis.cujae.edu.cu";
        int suffix = 1;
        while (used.contains(candidate) || personRepository.existsByEmail(candidate)) {
            candidate = base + suffix + "@ceis.cujae.edu.cu";
            suffix++;
        }
        used.add(candidate);
        return candidate;
    }

    private String uniqueCard(char sex, Set<String> used) {
        String candidate;
        do {
            candidate = generateCard(sex);
        } while (used.contains(candidate) || personRepository.existsByCard(candidate));
        used.add(candidate);
        return candidate;
    }

    /** Carné simulado de 11 dígitos: YYMMDD + 5 dígitos, con dígito de sexo en la posición 9. */
    private String generateCard(char sex) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int year = rnd.nextInt(1960, 2006);
        int month = rnd.nextInt(1, 13);
        int day = rnd.nextInt(1, 29);
        StringBuilder card = new StringBuilder();
        card.append(String.format("%02d%02d%02d", year % 100, month, day));
        for (int i = 0; i < 5; i++) {
            if (i == 3) {
                int[] female = {1, 3, 5, 7, 9};
                int[] male = {0, 2, 4, 6, 8};
                int[] pool = (sex == 'F') ? female : male;
                card.append(pool[rnd.nextInt(pool.length)]);
            } else {
                card.append(rnd.nextInt(10));
            }
        }
        return card.toString();
    }
}
