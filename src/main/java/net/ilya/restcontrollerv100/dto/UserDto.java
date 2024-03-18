package net.ilya.restcontrollerv100.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.entity.UserRole;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Сущность пользователя")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    private Long id;
    @Schema(description = "Login")
    @NotNull(message = "login for u account")
    private String username;
    @Schema(description= "Password",accessMode = Schema.AccessMode.READ_ONLY)
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "password for u account")
    private String password;
    private UserRole role;
    @Schema(description = "First name")
    @NotNull(message = "password for u account")
    private String firstName;
    @Schema(description = "Last name")
    @NotNull(message = "password for u account")
    private String lastName;
    private StatusEntity status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<EventDto> eventDto;

}
