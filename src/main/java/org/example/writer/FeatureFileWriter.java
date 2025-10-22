package org.example.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FeatureFileWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureFileWriter.class);

    public void write(String content, String outputPath) throws IOException {
        LOGGER.info("📝 Escribiendo archivo: {}", outputPath);

        Path path = Paths.get(outputPath);

        // Crear directorios si no existen
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
            LOGGER.debug("  ✓ Directorios creados: {}", parentDir);
        }

        // Escribir archivo
        try (BufferedWriter writer = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            writer.write(content);
            LOGGER.info("✅ Archivo escrito exitosamente: {}", outputPath);
        }
    }
}