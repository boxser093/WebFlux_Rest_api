package net.ilya.restcontrollerv100.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.ilya.restcontrollerv100.entity.StatusEntity;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Сущность события")
public class EventDto {

    private Long id;
    @Schema(description = "Id пользователя")
    private Long userId;
    @Schema(description = "Id файла")
    private Long fileId;
    private StatusEntity status;
    @JsonIgnore
    private UserDto userDto;
    @JsonIgnore
    private FileDto fileDto;
}
