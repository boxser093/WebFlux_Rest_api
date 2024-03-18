package net.ilya.restcontrollerv100.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "User controller", description = "Операции с пользователями")
@RequestMapping("/api/v1/users")
public class UserRestControllerV1 {

    private final UserService userService;
    private final UserMapper userMapper;

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Получить пользователя по id",
            description = "Вы можете получить информацию по пользователю по его ID")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/{id}")
    public Mono<UserDto> getUserById(@PathVariable @Parameter(description = "Идентификатор пользователя",
            in = ParameterIn.PATH, name = "id", required = true, schema = @Schema(
            defaultValue = "1",
            minimum = "1",
            allOf = {Integer.class} ),
            style = ParameterStyle.SIMPLE) Long id) {
        return userService.findById(id).map(userMapper::map);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Создать нового пользователя",
            description = "Вы можете нового пользователя")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/")
    public Mono<UserDto> createNewUser(@RequestBody @Parameter(description = "Информация о пользователе",
            schema = @Schema(additionalPropertiesSchema = UserDto.class)) UserDto userDto) {
        log.info("IN UserRestControllerV1, createNewUser input{} userDto", userDto);
        return userService.create(userMapper.map(userDto)).map(userMapper::map);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Обновить уже имеющиегося пользователя",
            description = "Вы можете обновить данные по уже имеющемуся пользователю")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/")
    public Mono<UserDto> updateUser(@RequestBody @Parameter(description = "Новая информация о пользователе",
            schema = @Schema(additionalPropertiesSchema = UserDto.class)) UserDto userDto) {
        return userService.update(userMapper.map(userDto)).map(userMapper::map);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Мягкое удаление пользователя",
            description = "Вы можете удалить ранее созданного Вами юзера")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public Mono<UserDto> deletedUser(@PathVariable @Validated
                                         @Parameter(description = "Идентификатор пользователя",
            in = ParameterIn.PATH, name = "id", required = true, schema = @Schema(
            defaultValue = "1",
            minimum = "1",
            allOf = {Integer.class} ),
            style = ParameterStyle.SIMPLE) Long id) {
        return userService.delete(id).map(userMapper::map);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Получить список всех пользователей",
            description = "Вы можете получить полный список все пользователей, со всеми статусами")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/")
    public Flux<UserDto> getAllUsers() {
        return userService.findAll();
    }
}
