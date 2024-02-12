package net.ilya.restcontrollerv100.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
public class FileDto {
    private Long id;
    private String fileName;
    private String filePath;
}
