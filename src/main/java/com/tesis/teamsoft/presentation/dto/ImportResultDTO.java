package com.tesis.teamsoft.presentation.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResultDTO {
    private int created;
    private int updated;
    private int skipped;
    private int errors;
    private List<String> errorMessages = new ArrayList<>();
}
