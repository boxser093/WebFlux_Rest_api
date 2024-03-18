package net.ilya.restcontrollerv100.rest;

import net.ilya.restcontrollerv100.entity.EventEntity;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.entity.UserEntity;
import net.ilya.restcontrollerv100.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRestControllerV1Test {
    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient webTestClient;
    @Test
    @WithMockUser(authorities = "ADMIN")
    void getUserById() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).configureClient()
                .responseTimeout(Duration.ofHours(1)).build();

        //given
        var eventEnt1 = EventEntity.builder()
                .userId(1L)
                .fileId(2L)
                .userId(7L)
                .build();
        var eventEnt2 = EventEntity.builder()
                .userId(5L)
                .fileId(9L)
                .userId(7L)
                .build();
        var userEntity = UserEntity.builder()
                .id(7L)
                .username("foo")
                .role(UserRole.USER)
                .firstName("Guts")
                .lastName("Berserk")
                .status(StatusEntity.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .eventEntityList(List.of(eventEnt1, eventEnt2))
                .build();

//        Mockito.when(userRepository.findById(7L)).thenReturn(Mono.just(userEntity));

        WebTestClient.ResponseSpec exchange = webTestClient.get().uri("/api/v1/users/7")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createNewUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deletedUser() {
    }

    @Test
    void getAllUsers() {
    }
}