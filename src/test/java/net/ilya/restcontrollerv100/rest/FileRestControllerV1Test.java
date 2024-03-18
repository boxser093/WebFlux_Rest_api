package net.ilya.restcontrollerv100.rest;

import lombok.val;
import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.dto.FileDto;
import net.ilya.restcontrollerv100.entity.EventEntity;
import net.ilya.restcontrollerv100.entity.FileEntity;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.mapper.EventMapper;
import net.ilya.restcontrollerv100.mapper.FileMapper;
import net.ilya.restcontrollerv100.security.SecurityService;
import net.ilya.restcontrollerv100.security.TokenDetails;
import net.ilya.restcontrollerv100.service.FileService;
import org.assertj.core.util.Files;
import org.flywaydb.core.internal.resource.classpath.ClassPathResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import static org.xmlunit.builder.Input.fromFile;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FileRestControllerV1Test {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private FileService fileService;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private SecurityService securityService;

    @Test
    void get_File_By_Id_admin_credentials() {
        String token = "Barrier ";
        TokenDetails tokenDetails = securityService.authenticate("admin", "admin").block();
        token += tokenDetails.getToken();

        FileEntity fileEntity = FileEntity.builder()
                .fileName("fail_Name_in_get_By_Id" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("fail_Path_in_get_By_Id" + Math.round(Math.random() + (Math.random() * 999)))
                .build();
        FileEntity fileEntityBeforeSave = fileService.create(fileEntity).block();

        webTestClient.get().uri("/api/v1/files/{id}", fileEntityBeforeSave.getId())
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(fileEntityBeforeSave.getId())
                .jsonPath("$.file_name").isEqualTo(fileEntityBeforeSave.getFileName())
                .jsonPath("$.file_path").isEqualTo(fileEntityBeforeSave.getFilePath());
    }

    @Test
    void create_New_File_admin_credentials() {

        String token = "Barrier ";
        TokenDetails tokenDetails = securityService.authenticate("admin", "admin").block();
        token += tokenDetails.getToken();

        String nameFile = String.format("testFile%d.txt", Math.round(Math.random() * 999));
        String filePath = "src/test/resources/uploads";
        String folder = "/newFolder37/";

        FileDto fileDto = fileMapper.map(FileEntity.builder()
                .fileName(nameFile)
                .filePath(filePath)
                .build());
        Path path = Path.of(fileDto.getFilePath());
        File file = Files.newFile(filePath + folder + nameFile);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(file));
        webTestClient.post().uri("/api/v1/files/")
                .header("Authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.file_name").isEqualTo(fileDto.getFileName())
                .jsonPath("$.file_path").isEqualTo(path.toString())
                .consumeWith(System.out::println);


    }

    @Test
    void update_File_admin_credentials() {
    }

    @Test
    void deleted_File_admin_credentials() {
        String token = "Barrier ";
        TokenDetails block = securityService.authenticate("admin", "admin").block();
        token += block.getToken();

        FileEntity fileEntityBeforeDeleted = FileEntity.builder()
                .fileName("fail_Name_in_deleted_By_Id" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("fail_Path_in_deleted_By_Id" + Math.round(Math.random() + (Math.random() * 999)))
                .build();

        FileEntity fileEntityBeforeSave = fileService.create(fileEntityBeforeDeleted).block();

        webTestClient.delete().uri("/api/v1/files/{id}", fileEntityBeforeSave.getId())
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(fileEntityBeforeSave.getId())
                .jsonPath("$.status").isEqualTo(StatusEntity.DELETED.name());
    }

    @Test
    void get_Files_admin_credentials() {
        String token = "Barrier ";
        TokenDetails block = securityService.authenticate("admin", "admin").block();
        token += block.getToken();

        webTestClient.get().uri("/api/v1/files/")
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FileDto.class)
                .consumeWith(System.out::println);
    }
}