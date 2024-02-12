package net.ilya.restcontrollerv100.rest;

import lombok.RequiredArgsConstructor;

import net.ilya.restcontrollerv100.dto.AuthRequestDto;
import net.ilya.restcontrollerv100.dto.AuthResponseDto;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.UserEntity;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.security.SecurityService;
import net.ilya.restcontrollerv100.service.Impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AuthRestControllerV1 {

    private final SecurityService securityService;
    private final UserServiceImpl userServiceImpl;
    private final UserMapper userMapper;

    @PostMapping("/api/v1/auth/register")
    public Mono<UserDto> register(@RequestBody UserDto dto) {
        UserEntity entity = userMapper.map(dto);
        return userServiceImpl.registerUser(entity).map(userMapper::map);
    }

    @PostMapping("/api/v1/auth/login")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
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
