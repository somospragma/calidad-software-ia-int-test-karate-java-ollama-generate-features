package org.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();

        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new ConfigPropertyException("No se encontró el archivo: " + CONFIG_FILE);
            }

            properties.load(input);
            LOGGER.debug("✅ Configuración cargada correctamente");

        } catch (IOException e) {
            throw new ConfigPropertyException("Error cargando configuración", e);
        }
    }

    public static String getPropertyByKey(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            LOGGER.warn("⚠️  Propiedad no encontrada: {}", key);
            return "";
        }

        return value;
    }

    public static String getPropertyByKey(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static class ConfigPropertyException extends RuntimeException {
        public ConfigPropertyException(String message) {
            super(message);
        }

        public ConfigPropertyException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}