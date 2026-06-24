package com.tesis.teamsoft.service.implementation;

import com.tesis.teamsoft.exception.BusinessRuleException;
import com.tesis.teamsoft.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Almacenamiento temporal de los CSV subidos en el paso /import/parse.
 * Guarda cada archivo bajo {tmpdir}/teamsoft-imports/{uuid}.csv y lo referencia
 * por un fileId (UUID) en el paso /import/execute. Mantiene el flujo stateless.
 */
@Component
public class ImportFileStorage {

    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F-]{36}$");

    private final Path baseDir = Path.of(System.getProperty("java.io.tmpdir"), "teamsoft-imports");

    /** Persiste el contenido del archivo y devuelve el fileId generado. */
    public String store(byte[] content) {
        try {
            Files.createDirectories(baseDir);
            String fileId = UUID.randomUUID().toString();
            Files.write(resolve(fileId), content);
            return fileId;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Ruta del archivo temporal; valida el fileId para evitar path traversal. */
    public Path resolve(String fileId) {
        if (fileId == null || !UUID_PATTERN.matcher(fileId).matches()) {
            throw new BusinessRuleException("ERR_IMPORT_INVALID_FILE_ID");
        }
        return baseDir.resolve(fileId + ".csv");
    }

    /** Lee el archivo temporal; 404 si no existe (expiró o fileId inválido). */
    public byte[] read(String fileId) {
        Path path = resolve(fileId);
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("ERR_IMPORT_FILE_NOT_FOUND", fileId);
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Elimina físicamente el archivo temporal (limpieza tras la importación). */
    public void delete(String fileId) {
        try {
            Files.deleteIfExists(resolve(fileId));
        } catch (IOException ignored) {
            // limpieza best-effort: un temporal huérfano no debe romper la importación
        }
    }
}
