package net.ilya.restcontrollerv100.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Сущность пользователя")
public class AuthRequestDto {
    @Schema(description = "Логин")
    private String username;
    @Schema(description = "Пароль")
    private String password;
}