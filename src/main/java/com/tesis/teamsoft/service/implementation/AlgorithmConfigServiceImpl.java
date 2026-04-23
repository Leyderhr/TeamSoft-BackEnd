package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.config.AlgorithmConfig;
import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.presentation.dto.AlgorithmConfigDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlgorithmConfigServiceImpl{

    public void saveAlgorithmConfig(AlgorithmConfigDTO.AlgorithmConfigUpdateDTO configDTO) {
        String filePath = AlgorithmConfig.getConfigFilePath();
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Failed to read configuration file: {}", filePath, e);
            throw new BusinessRuleException("Unable to read current configuration: " + e.getMessage());
        }

        updatePropertiesFromDTO(properties, configDTO);

        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, "Updated via API - " + LocalDateTime.now());
            output.flush();
            log.info("Configuration saved to: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to write configuration file: {}", filePath, e);
            throw new BusinessRuleException("Unable to save configuration: " + e.getMessage());
        }

        AlgorithmConfig.reload();
        log.info("Configuration reloaded in memory");
    }

    private void updatePropertiesFromDTO(Properties props, AlgorithmConfigDTO.AlgorithmConfigUpdateDTO dto) {
        setPropertyIfNotNull(props, "initialSolutionConf", dto.getInitialSolutionConf());
        setPropertyIfNotNull(props, "numberPersonTries", dto.getNumberPersonTries());
        setPropertyIfNotNull(props, "operatorOpc", dto.getOperatorOpc());
        setPropertyIfNotNull(props, "operatorTypeOpc", dto.getOperatorTypeOpc());
        setPropertyIfNotNull(props, "executions", dto.getExecutions());
        setPropertyIfNotNull(props, "iterations", dto.getIterations());
        setPropertyIfNotNull(props, "calculateTime", dto.isCalculateTime());
        setPropertyIfNotNull(props, "validate", dto.isValidate());
        setPropertyIfNotNull(props, "possibleValidateNumber", dto.getPossibleValidateNumber());
        setPropertyIfNotNull(props, "HillClimbingRestartCount", dto.getHillClimbingRestartCount());
        setPropertyIfNotNull(props, "TabuSolutionsMaxelements", dto.getTabuSolutionsMaxelements());
        setPropertyIfNotNull(props, "MultiobjectiveHCRestartSizeNeighbors", dto.getMultiobjectiveHCRestartSizeNeighbors());
        setPropertyIfNotNull(props, "MultiobjectiveHCDistanceSizeNeighbors", dto.getMultiobjectiveHCDistanceSizeNeighbors());
        setPropertyIfNotNull(props, "MultiobjectiveTabuSolutionsMaxelements", dto.getMultiobjectiveTabuSolutionsMaxelements());
    }

    private void setPropertyIfNotNull(Properties props, String key, Object value) {
        if (value != null) {
            props.setProperty(key, String.valueOf(value));
        }
    }
}