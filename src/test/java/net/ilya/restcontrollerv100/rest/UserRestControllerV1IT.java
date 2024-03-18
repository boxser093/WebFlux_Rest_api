package net.ilya.restcontrollerv100.rest;

import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.entity.UserEntity;
import net.ilya.restcontrollerv100.entity.UserRole;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.security.SecurityService;
import net.ilya.restcontrollerv100.security.TokenDetails;
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

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserRestControllerV1IT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SecurityService securityService;

    @Test
    void get_User_By_Id_in_admin_credential() {
        String token = "Barrier ";
        TokenDetails block = securityService.authenticate("admin", "admin").block();
        token += block.getToken();
        UserEntity userEntity = userService.findById(1L).block();

        webTestClient.get().uri("/api/v1/users/1")
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(userEntity.getId())
                .jsonPath("$.first_name").isEqualTo(userEntity.getFirstName())
                .jsonPath("$.last_name").isEqualTo(userEntity.getLastName())
                .jsonPath("$.role").isEqualTo(userEntity.getRole().name());

    }

    @Test
    void create_New_User_in_admin_credential() {
        String token = "Barrier ";
        TokenDetails block = securityService.authenticate("admin", "admin").block();
        token += block.getToken();

        UserEntity testUser = UserEntity.builder()
                .firstName("Guts"+Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Berserk"+Math.round(Math.random() + (Math.random() * 999)))
                .username("test"+Math.round(Math.random() + (Math.random() * 999)))
                .password("test"+Math.round(Math.random() + (Math.random() * 999)))
                .role(UserRole.USER)
                .build();

        webTestClient.post().uri("/api/v1/users/")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userMapper.map(testUser)), UserDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.first_name").isEqualTo(testUser.getFirstName())
                .jsonPath("$.last_name").isEqualTo(testUser.getLastName())
                .jsonPath("$.role").isEqualTo(testUser.getRole().name());

    }

    @Test
    void update_User_in_admin_credential() {
        String token = "Barrier ";
        TokenDetails block = securityService.authenticate("admin", "admin").block();
        token += block.getToken();

        UserEntity updateUser = UserEntity.builder()
                .firstName("Jimmy"+Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Cho"+Math.round(Math.random() + (Math.random() * 999)))
                .username("test"+Math.round(Math.random() + (Math.random() * 999)))
                .password("test"+Math.round(Math.random() + (Math.random() * 999)))
                .role(UserRole.USER)
                .build();

        UserEntity saveUser = userService.create(updateUser).block();
        saveUser.setFirstName("Jonny");
        saveUser.setLastName("Cage");

        webTestClient.put().uri("/api/v1/users/")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userMapper.map(saveUser)), UserDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.first_name").isEqualTo(saveUser.getFirstName())
                .jsonPath("$.last_name").isEqualTo(saveUser.getLastName())
                .jsonPath("$.role").isEqualTo(saveUser.getRole().name());
    }

    @Test
    void deleted_User_in_admin_credential() {
        String token = "Barrier ";
        TokenDetails block = securityService.authenticate("admin", "admin").block();
        token += block.getToken();

        UserEntity newUser = userService.create(UserEntity.builder()
                .firstName("Jimmy"+Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Cho"+Math.round(Math.random() + (Math.random() * 999)))
                .username("test"+Math.round(Math.random() + (Math.random() * 999)))
                .password("test"+Math.round(Math.random() + (Math.random() * 999)))
                .role(UserRole.USER)
                .build()).block();

        webTestClient.delete().uri("/api/v1/users/{id}", newUser.getId())
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(newUser.getId())
                .jsonPath("$.status").isEqualTo(StatusEntity.DELETED.name());
    }

    @Test
    void get_All_Users_in_admin_credential() {
        String token = "Barrier ";
        TokenDetails block = securityService.authenticate("admin", "admin").block();
        token += block.getToken();
        webTestClient.get().uri("/api/v1/users/")
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .consumeWith(System.out::println);
    }
}