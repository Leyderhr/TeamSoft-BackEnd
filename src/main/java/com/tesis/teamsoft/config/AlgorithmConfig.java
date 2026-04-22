package com.tesis.teamsoft.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Component
@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AlgorithmConfig {

    private static final Properties properties = new Properties();
    @Getter
    private static String configFilePath;

    @Value("${algorithm.config.path:${user.home}/.teamsoft/config/}")
    private String externalConfigPath;

    @PostConstruct
    private void init() {
        try {
            configFilePath = Paths.get(externalConfigPath, "algorithmConf.properties").toString();
            ensureConfigFileExists();
            loadProperties();

            log.info("Algorithm configuration loaded from: {}", configFilePath);
        } catch (IOException e) {
            log.error("CRITICAL: Could not initialize algorithm configuration", e);
        }
    }

    /**
     * Crea el directorio si no existe y, si el archivo no está presente,
     * copia la versión por defecto desde el classpath.
     */
    private void ensureConfigFileExists() throws IOException {
        Path configPath = Paths.get(configFilePath);
        Path parentDir = configPath.getParent();

        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
            log.info("Created configuration directory: {}", parentDir);
        }

        if (!Files.exists(configPath)) {
            try (InputStream defaultConfigStream = new ClassPathResource("algorithmConf.properties").getInputStream()) {
                Files.copy(defaultConfigStream, configPath);
                log.info("Default configuration copied to: {}", configPath);
            } catch (IOException e) {
                log.error("Failed to copy default configuration. Does algorithmConf.properties exist in src/main/resources?");
                throw e;
            }
        }
    }

    /**
     * Carga las propiedades desde el archivo externo al objeto estático 'properties'.
     */
    private static void loadProperties() throws IOException {
        try (InputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        }
    }

    /**
     * Recarga la configuración desde el archivo externo.
     * Debe llamarse después de que el archivo sea modificado externamente (por ejemplo, desde la API).
     */
    public static void reload() {
        try {
            loadProperties();
            log.info("Algorithm configuration reloaded from: {}", configFilePath);
        } catch (IOException e) {
            log.error("Failed to reload configuration", e);
        }
    }

    // ---------- Métodos de acceso a propiedades ----------
    public static String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid integer for key '{}': '{}'. Using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    public static float getFloat(String key, float defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid float for key '{}': '{}'. Using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

}
