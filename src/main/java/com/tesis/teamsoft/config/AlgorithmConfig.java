package com.tesis.teamsoft.config;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Component
@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AlgorithmConfig {

    private static ResourceBundle bundle;
    private static final String BUNDLE_NAME = "algorithmConf";

    static {
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME);
            log.info("Algorithm configuration loaded successfully from {}.properties", BUNDLE_NAME);
        } catch (MissingResourceException e) {
            log.error("CRITICAL: Algorithm configuration file not found: {}.properties", BUNDLE_NAME);
            log.error("Using default values. Please create the configuration file in src/main/resources/");
        }
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        try {
            if (bundle != null) {
                return bundle.getString(key);
            }
        } catch (MissingResourceException e) {
            log.warn("Configuration key '{}' not found, using default: {}", key, defaultValue);
        }
        return defaultValue;
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static int getInt(String key, int defaultValue) {
        try {
            String value = getString(key);
            if (value != null) {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            log.warn("Configuration key '{}' is not a valid integer, using default: {}", key, defaultValue);
        }
        return defaultValue;
    }

    public static float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    public static float getFloat(String key, float defaultValue) {
        try {
            String value = getString(key);
            if (value != null) {
                return Float.parseFloat(value);
            }
        } catch (NumberFormatException e) {
            log.warn("Configuration key '{}' is not a valid float, using default: {}", key, defaultValue);
        }
        return defaultValue;
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        try {
            String value = getString(key);
            if (value != null) {
                return Boolean.parseBoolean(value);
            }
        } catch (Exception e) {
            log.warn("Configuration key '{}' is not a valid boolean, using default: {}", key, defaultValue);
        }
        return defaultValue;
    }

    public static void reload() {
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME);
            log.info("Algorithm configuration reloaded successfully");
        } catch (MissingResourceException e) {
            log.error("Failed to reload algorithm configuration");
        }
    }
}
