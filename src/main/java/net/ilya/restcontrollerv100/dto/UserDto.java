package net.ilya.restcontrollerv100.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.entity.UserRole;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

@Data
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    private Long id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private UserRole role;
    private String firstName;
    private String lastName;
    private StatusEntity status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Flux<EventDto> eventDtoList;
}
