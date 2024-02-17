package net.ilya.restcontrollerv100.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import net.ilya.restcontrollerv100.entity.StatusEntity;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EventDto {
    private List<UserDto> userDtoList;
    private List<FileDto> fileDtoList;
    private Long id;
    private Long userId;
    private Long fileId;
    private StatusEntity status;

}
