package com.tesis.teamsoft.service.interfaces;

import com.tesis.teamsoft.presentation.dto.ImportDTO;
import com.tesis.teamsoft.presentation.dto.ImportResultDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IImportService {

    /** Paso 1: lee el CSV, valida homogeneidad y lo guarda temporalmente. */
    ImportDTO.ParseResponseDTO parse(MultipartFile file);

    /** Paso 2: ejecuta la importación con el mapeo de configuración. */
    ImportResultDTO execute(ImportDTO.ExecuteRequestDTO request);
}
