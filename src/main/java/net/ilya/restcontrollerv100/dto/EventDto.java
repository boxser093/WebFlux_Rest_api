package net.ilya.restcontrollerv100.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import net.ilya.restcontrollerv100.entity.FileEntity;
import net.ilya.restcontrollerv100.entity.UserEntity;
@Data
public class EventDto {
    private Long id;
    private UserEntity user;
    private FileEntity file;
}
