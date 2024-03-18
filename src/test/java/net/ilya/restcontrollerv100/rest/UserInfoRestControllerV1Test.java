package net.ilya.restcontrollerv100.rest;

import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.EventEntity;
import net.ilya.restcontrollerv100.entity.FileEntity;
import net.ilya.restcontrollerv100.entity.UserEntity;
import net.ilya.restcontrollerv100.entity.UserRole;
import net.ilya.restcontrollerv100.security.SecurityService;
import net.ilya.restcontrollerv100.security.TokenDetails;
import net.ilya.restcontrollerv100.service.EventService;
import net.ilya.restcontrollerv100.service.FileService;
import net.ilya.restcontrollerv100.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserInfoRestControllerV1Test {
    @Autowired
    private SecurityService securityService;
    @Autowired
    private FileService fileService;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void get_User_Info_all_credential() {
        String token = "Barrier ";
        TokenDetails tokenDetails = securityService.authenticate("admin", "admin").block();
        token += tokenDetails.getToken();

        UserEntity build = UserEntity.builder()
                .firstName("Defolt_User_in_info" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Defolt_LastName" + Math.round(Math.random() + (Math.random() * 999)))
                .username("test" + Math.round(Math.random() + (Math.random() * 999)))
                .password("test" + Math.round(Math.random() + (Math.random() * 999)))
                .role(UserRole.USER)
                .build();
        UserEntity newUser = userService.create(build).block();

        FileEntity fileEntity = FileEntity.builder()
                .fileName("fail_Name_in_get_By_Id" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("fail_Path_in_get_By_Id" + Math.round(Math.random() + (Math.random() * 999)))
                .build();
        FileEntity fileEntityBeforeSave = fileService.create(fileEntity).block();

        EventEntity forGetId = EventEntity.builder()
                .fileId(fileEntityBeforeSave.getId())
                .userId(newUser.getId())
                .build();

        EventEntity block = eventService.create(forGetId).block();

        String tokenUser = "Barrier ";
        TokenDetails tokenDetailsUsers = securityService.authenticate(build.getUsername(), build.getPassword()).block();
        tokenUser += tokenDetailsUsers.getToken();
        webTestClient.get().uri("/api/v1/users/info/")
                .header("Authorization", tokenUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .consumeWith(result -> {
                    result.getStatus().is2xxSuccessful();
                    result.getResponseBody().getId().equals(newUser.getId());
                    result.getResponseBody().getFirstName().equals(newUser.getFirstName());
                    result.getResponseBody().getEventDto().equals(newUser.getEventEntityList());
                });
    }
}