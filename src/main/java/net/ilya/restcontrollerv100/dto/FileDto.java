package net.ilya.restcontrollerv100.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.ilya.restcontrollerv100.entity.StatusEntity;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Сущность файла")
public class FileDto {
    private Long id;
    @Schema(description = "Имя файла")
    private String fileName;
    @Schema(description = "Путь сохранения")
    private String filePath;
    @Schema(description = "Cтатус файла")
    private StatusEntity status;
    @JsonIgnore
    private EventDto eventDto;

}
