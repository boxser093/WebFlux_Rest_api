package net.ilya.restcontrollerv100.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.security.CustomPrincipal;
import net.ilya.restcontrollerv100.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/info")
@Tag(name = "Info controller", description = "Получение инфорации пользователя с ролью USER")
public class UserInfoRestControllerV1 {
    private final UserService userService;
    private final UserMapper userMapper;
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Получить информацию по пользователю и списку загруженных файлов",
            description = "Вы можете получить инфорамцию по пользователю со всеми загруженными файлами")
    @GetMapping("/")
    public Mono<UserDto> getUserInfo(@Parameter(name = "JWT Token",
            description = "Вставьте JWT токен в поле аутентификации")
                                     Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        log.info("IN InfoRestControllerV1 getUserInfo ID  - {}", customPrincipal.getId());
        return userService.findByIdWithEvents(customPrincipal.getId()).map(userMapper::map);
    }
}
