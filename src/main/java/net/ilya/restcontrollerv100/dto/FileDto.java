package net.ilya.restcontrollerv100.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import net.ilya.restcontrollerv100.entity.StatusEntity;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FileDto {
    private Long id;
    @JsonProperty("file_Name")
    private String fileName;
    @JsonProperty("file_Path")
    private String filePath;
    private StatusEntity fileStatus;
}
