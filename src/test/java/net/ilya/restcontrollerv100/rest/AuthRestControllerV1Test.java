package net.ilya.restcontrollerv100.rest;

import net.ilya.restcontrollerv100.dto.AuthRequestDto;
import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.UserEntity;
import net.ilya.restcontrollerv100.entity.UserRole;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.security.SecurityService;
import net.ilya.restcontrollerv100.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthRestControllerV1Test {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Test
    void register_new_user() {
        UserEntity userEntityRegister = UserEntity.builder()
                .firstName("Guts_register"+Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Berserk_register"+Math.round(Math.random() + (Math.random() * 999)))
                .username("reg"+Math.round(Math.random() + (Math.random() * 999)))
                .password("reg"+Math.round(Math.random() + (Math.random() * 999)))
                .build();

        webTestClient.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userMapper.map(userEntityRegister)), UserDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .consumeWith(System.out::println)
                .jsonPath("$.first_name").isEqualTo(userEntityRegister.getFirstName())
                .jsonPath("$.last_name").isEqualTo(userEntityRegister.getLastName())
                .jsonPath("$.username").isEqualTo(userEntityRegister.getUsername());
    }

    @Test
    void login_register_user() {
        UserEntity userEntityLogin = UserEntity.builder()
                .firstName("Guts_Login"+Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Berserk_Login"+Math.round(Math.random() + (Math.random() * 999)))
                .username("log"+Math.round(Math.random() + (Math.random() * 999)))
                .password("log"+Math.round(Math.random() + (Math.random() * 999)))
                .build();
        UserEntity saveUser = userService.registerUser(userEntityLogin).block();

        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username(userEntityLogin.getUsername())
                .password(userEntityLogin.getPassword())
                .build();

        webTestClient.post().uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), AuthRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .consumeWith(System.out::println)
                .jsonPath("$.user_id").isEqualTo(saveUser.getId());
    }
}