package net.ilya.restcontrollerv100.rest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.AuthRequestDto;
import net.ilya.restcontrollerv100.dto.AuthResponseDto;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.UserEntity;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.security.SecurityService;
import net.ilya.restcontrollerv100.service.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Authenticate controller", description = "Регистрация и логин пользователя")
@RequestMapping("/api/v1/auth")
public class AuthRestControllerV1 {

    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;
    @Operation(summary = "Регистрация нового пользователя",
            description = "Вы можете создать нового пользователя и придумать ему пароль")
    @PostMapping(value = "/register", produces = "application/json")
    public Mono<UserDto> register(@RequestBody @Parameter(description = "Информация о пользователе",
            schema = @Schema(additionalPropertiesSchema = UserDto.class)) UserDto dto) {
        log.info("IN AuthRestControllerV1 input DTO:{}",dto);
        return userService.registerUser(userMapper.map(dto)).map(userMapper::map);
    }
    @Operation(summary = "Логин в систему",
            description = "Вход в систему и получение JWT для последующих операций")
    @PostMapping(value = "/login", produces = "application/json")
    public Mono<AuthResponseDto> login(@RequestBody @Parameter(description = "Введи логин и пароль ранее зарегистрированного пользователя",
    schema = @Schema(additionalPropertiesSchema = AuthRequestDto.class)) AuthRequestDto dto) {
        return securityService.authenticate(dto.getUsername(), dto.getPassword())
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDto.builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssuedAt())
                                .expiresAt(tokenDetails.getExpiresAt())
                                .build()
                ));
    }
}
