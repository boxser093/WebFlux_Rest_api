package net.ilya.restcontrollerv100.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.security.CustomPrincipal;
import net.ilya.restcontrollerv100.service.Impl.UserServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserRestControllerV1 {

    private final UserServiceImpl userServiceImpl;
    private final UserMapper userMapper;


    @GetMapping("/api/v1/users/info")
    public Mono<UserDto> getUserInfo(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();

        return userServiceImpl.findById(customPrincipal.getId())
                .map(userMapper::map);

    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/api/v1/users/{id}")
    public Mono<UserDto> getUserById(@PathVariable Long id) {
        return userServiceImpl.findById(id).map(userMapper::map);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/api/v1/users/")
    public Mono<UserDto> createNewUser(@RequestBody UserDto userDto) {
        log.info("IN UserRestControllerV1, createNewUser input{} userDto", userDto);
        return userServiceImpl.create(userMapper.map(userDto)).map(userMapper::map);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/api/v1/users/")
    public Mono<UserDto> updateUser(@RequestBody UserDto userDto) {
        return userServiceImpl.update(userMapper.map(userDto)).map(userMapper::map);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/api/v1/users/{id}")
    public Mono<UserDto> deletedUser(@PathVariable @Validated Long id) {
        return userServiceImpl.delete(id).map(userMapper::map);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/api/v1/users/")
    public Flux<UserDto> getAllUsers() {

        return userServiceImpl.findAll();
    }
}
