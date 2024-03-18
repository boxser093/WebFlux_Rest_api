package net.ilya.restcontrollerv100.rest;

import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.entity.*;
import net.ilya.restcontrollerv100.mapper.EventMapper;
import net.ilya.restcontrollerv100.security.SecurityService;
import net.ilya.restcontrollerv100.security.TokenDetails;
import net.ilya.restcontrollerv100.service.EventService;
import net.ilya.restcontrollerv100.service.FileService;
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
class EventRestControllerV1IT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private EventService eventService;
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;

    @Test
    void get_Event_By_Id_admin_credentials() {
        String token = "Barrier ";
        TokenDetails tokenDetails = securityService.authenticate("admin", "admin").block();
        token += tokenDetails.getToken();

        EventEntity forGetId = EventEntity.builder()
                .fileId(Math.round(Math.random() + (Math.random() * 999)))
                .userId(Math.round(Math.random() + (Math.random() * 999)))
                .build();
        EventEntity eventBeforeSave = eventService.create(forGetId).block();


        System.out.println(eventBeforeSave);
        webTestClient.get().uri("/api/v1/events/{id}", eventBeforeSave.getId())
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(eventBeforeSave.getId())
                .jsonPath("$.user_id").isEqualTo(eventBeforeSave.getUserId())
                .jsonPath("$.file_id").isEqualTo(eventBeforeSave.getFileId());
    }

    @Test
    void get_Events_admin_credentials() {
        String token = "Barrier ";
        TokenDetails block = securityService.authenticate("admin", "admin").block();
        token += block.getToken();
        webTestClient.get().uri("/api/v1/events/")
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EventDto.class)
                .consumeWith(System.out::println);
    }

    @Test
    void create_New_Event_admin_credentials() {
        String token = "Barrier ";
        TokenDetails tokenDetails = securityService.authenticate("admin", "admin").block();
        token += tokenDetails.getToken();

        UserEntity block = userService.create(UserEntity.builder()
                .firstName("Sonya" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Blade" + Math.round(Math.random() + (Math.random() * 999)))
                .username("test" + Math.round(Math.random() + (Math.random() * 999)))
                .password("test" + Math.round(Math.random() + (Math.random() * 999)))
                .role(UserRole.USER)
                .build()).block();

        FileEntity block1 = fileService.create(FileEntity.builder()
                .fileName("imageSonya.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somepath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .build()).block();
        EventEntity block2 = eventService.create(EventEntity.builder()
                .userId(block.getId())
                .fileId(block1.getId())
                .status(StatusEntity.ACTIVE)
                .build()).block();

        webTestClient.post().uri("/api/v1/events/")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(eventMapper.map(block2)), EventDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.user_id").isEqualTo(block.getId())
                .jsonPath("$.file_id").isEqualTo(block1.getId());
    }

    @Test
    void update_Event_admin_credentials() {
        String token = "Barrier ";
        TokenDetails block = securityService.authenticate("admin", "admin").block();
        token += block.getToken();

        EventEntity saveEvent = eventService.create(EventEntity.builder()
                .userId(Math.round(Math.random() * 999 + Math.random()))
                .fileId(Math.round(Math.random() * 999 + Math.random()))
                .build()).block();

        EventEntity beforeUpdateEvent = eventService.update(saveEvent.toBuilder()
                .userId(Math.round(Math.random() * 999 + Math.random()))
                .fileId(Math.round(Math.random() * 999 + Math.random()))
                .build()).block();

        webTestClient.put().uri("/api/v1/events/")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(eventMapper.map(beforeUpdateEvent)), EventDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(saveEvent.getId())
                .jsonPath("$.user_id").isEqualTo(beforeUpdateEvent.getUserId())
                .jsonPath("$.file_id").isEqualTo(beforeUpdateEvent.getFileId());
    }

    @Test
    void deleted_Event_admin_credentials() {
        String token = "Barrier ";
        TokenDetails block = securityService.authenticate("admin", "admin").block();
        token += block.getToken();

        EventEntity saveEvent = eventService.create(EventEntity.builder()
                .userId(Math.round(Math.random() * 999 + Math.random()))
                .fileId(Math.round(Math.random() * 999 + Math.random()))
                .build()).block();

        webTestClient.delete().uri("/api/v1/events/{id}", saveEvent.getId())
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(saveEvent.getId())
                .jsonPath("$.status").isEqualTo(StatusEntity.DELETED.name());
    }
}