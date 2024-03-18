package net.ilya.restcontrollerv100.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.security.CustomPrincipal;
import net.ilya.restcontrollerv100.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserInfoRestControllerV1 {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/api/v1/users/info/test")
    public Mono<UserDto> getUserInfo(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        log.info("IN InfoRestControllerV1 getUserInfo ID  - {}", customPrincipal.getId());
        return userService.findByIdWithEvents(customPrincipal.getId()).map(userMapper::map);
    }
}
