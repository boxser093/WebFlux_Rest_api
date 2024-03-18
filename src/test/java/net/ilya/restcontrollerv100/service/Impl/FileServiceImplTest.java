package net.ilya.restcontrollerv100.service.Impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import net.ilya.restcontrollerv100.entity.*;
import net.ilya.restcontrollerv100.mapper.FileMapper;
import net.ilya.restcontrollerv100.repository.FileRepository;
import net.ilya.restcontrollerv100.service.EventService;
import net.ilya.restcontrollerv100.service.FileService;
import net.ilya.restcontrollerv100.service.FileStorageService;
import net.ilya.restcontrollerv100.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class FileServiceImplTest {
    @MockBean
    FileStorageService fileStorageService;
    @MockBean
    FileRepository fileRepository;
    @SpyBean
    FileMapper mapper;
    @MockBean
    EventService eventService;
    @MockBean
    AmazonS3 amazonS3;
    @MockBean
    UserService userService;
    @SpyBean
    FileService fileService;
    Path filePath;
    String bucketName;

    @BeforeEach
    public void setup() {
        fileService = new FileServiceImpl(fileStorageService, fileRepository, mapper, eventService, userService, amazonS3);
        filePath = Path.of("src/test/resources/uploads");
        bucketName = "backed-for-tests";
    }

    @Test
    void find_By_Id() {
        long testFileId = 5L;
        FileEntity file1 = FileEntity.builder()
                .id(testFileId)
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .status(StatusEntity.ACTIVE)
                .build();

        when(fileRepository.findById(testFileId)).thenReturn(Mono.just(file1));
        //then
        StepVerifier
                .create(fileService.findById(testFileId))
                .expectNextMatches(fileEntity -> fileEntity.getId().equals(5L)
                        && fileEntity.getFileName().equals(file1.getFileName())
                        && fileEntity.getFilePath().equals(file1.getFilePath()))
                .expectComplete()
                .verify();
    }

    @Test
    void create() {
        FileEntity file1 = FileEntity.builder()
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .build();

        FileEntity afterSave = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName(file1.getFileName())
                .filePath(file1.getFilePath())
                .status(StatusEntity.ACTIVE)
                .build();

        when(fileRepository.save(file1.toBuilder()
                .status(StatusEntity.ACTIVE)
                .build())).thenReturn(Mono.just(afterSave));
        //then
        StepVerifier
                .create(fileService.create(file1))
                .expectNextMatches(
                        fileEntity -> fileEntity.getId().equals(afterSave.getId())
                                && fileEntity.getFileName().equals(file1.getFileName())
                                && fileEntity.getFilePath().equals(file1.getFilePath())
                                && fileEntity.getStatus().equals(StatusEntity.ACTIVE))
                .expectComplete()
                .verify();
    }

    @Test
    void create_with_userClaims() {
        long testUserId = 5L;

        FileEntity file1 = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .status(StatusEntity.ACTIVE)
                .build();

        FileEntity file2 = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .status(StatusEntity.ACTIVE)
                .build();

        EventEntity event = EventEntity.builder()
                .id(1L)
                .status(StatusEntity.ACTIVE)
                .userId(testUserId)
                .fileId(file1.getId())
                .userEntity(null)
                .fileEntity(file1)
                .build();

        EventEntity event2 = EventEntity.builder()
                .id(2L)
                .status(StatusEntity.ACTIVE)
                .userId(testUserId)
                .fileId(file2.getId())
                .userEntity(null)
                .fileEntity(file2)
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id(5L)
                .firstName("Gabe" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Gabenov" + Math.round(Math.random() + (Math.random() * 999)))
                .username("test" + Math.round(Math.random() + (Math.random() * 999)))
                .password("test" + Math.round(Math.random() + (Math.random() * 999)))
                .role(UserRole.USER)
                .status(StatusEntity.ACTIVE)
                .eventEntityList(List.of(event, event2))
                .build();

        Claims userClaims = new DefaultClaims();
        userClaims.put("username", userEntity.getUsername());
        Mono<FilePart> exampleFile = Mono.just(mock(FilePart.class));
        String fileName = "newUploadFile" + Math.round(Math.random() + (Math.random() * 999));
        FileEntity uploadsFile = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName(fileName)
                .filePath(filePath.toString())
                .status(StatusEntity.ACTIVE)
                .build();
        EventEntity eventEntity = new EventEntity();
        EventEntity afterBuild = EventEntity.builder()
                .userEntity(userEntity)
                .fileEntity(uploadsFile)
                .fileId(userEntity.getId())
                .userId(uploadsFile.getId())
                .build();
        //when
        ReflectionTestUtils.setField(fileService, "filePath", filePath);

        when(userService.getUserByUsername((String) userClaims.get("username"))).thenReturn(Mono.just(userEntity));
        when(fileStorageService.save(exampleFile)).thenReturn(Mono.just(fileName));
        when(fileRepository.save(any(FileEntity.class))).thenReturn(Mono.just(uploadsFile));
        when(amazonS3.putObject(any())).thenReturn(Mockito.mock(PutObjectResult.class));
        when(eventService.create(eventEntity.toBuilder().build())).thenReturn(Mono.just(afterBuild));
        //then

        StepVerifier
                .create(fileService.create(userClaims, exampleFile))
                .expectNextMatches(
                        fileEntity -> fileEntity.getId().equals(uploadsFile.getId())
                                && fileEntity.getFileName().equals(uploadsFile.getFileName())
                                && fileEntity.getFilePath().equals(uploadsFile.getFilePath())
                                && fileEntity.getStatus().equals(StatusEntity.ACTIVE))
                .expectComplete()
                .verify();

    }

    @Test
    void update() {
        FileEntity beforeUpdate = FileEntity.builder()
                .id(5L)
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .status(StatusEntity.ACTIVE)
                .build();

        FileEntity afterUpdate = FileEntity.builder()
                .id(beforeUpdate.getId())
                .fileName("Aws3.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("new/path" + Math.round(Math.random() + (Math.random() * 999)))
                .status(StatusEntity.UPGRADE)
                .build();

        when(fileRepository.findById(beforeUpdate.getId())).thenReturn(Mono.just(beforeUpdate));
        when(fileRepository.save(
                beforeUpdate.toBuilder()
                        .filePath(afterUpdate.getFilePath())
                        .fileName(afterUpdate.getFileName())
                        .status(StatusEntity.UPGRADE)
                        .build())).thenReturn(Mono.just(afterUpdate));
        //then
        StepVerifier
                .create(fileService.update(afterUpdate.toBuilder()
                        .status(StatusEntity.ACTIVE)
                        .build()))
                .expectNextMatches(
                        fileEntity -> fileEntity.getId().equals(beforeUpdate.getId())
                                && fileEntity.getFileName().equals(afterUpdate.getFileName())
                                && fileEntity.getFilePath().equals(afterUpdate.getFilePath())
                                && fileEntity.getStatus().equals(StatusEntity.UPGRADE))
                .expectComplete()
                .verify();

    }

    @Test
    void delete() {
        long testFileId = 5L;
        FileEntity file1 = FileEntity.builder()
                .id(testFileId)
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .status(StatusEntity.ACTIVE)
                .build();
        FileEntity afterDeleted = FileEntity.builder()
                .id(testFileId)
                .fileName(file1.getFileName())
                .filePath(file1.getFilePath())
                .status(StatusEntity.DELETED)
                .build();
        when(fileRepository.findById(testFileId)).thenReturn(Mono.just(file1));
        when(fileRepository.save(afterDeleted)).thenReturn(Mono.just(file1.toBuilder()
                .status(StatusEntity.DELETED)
                .build()));
        //then
        StepVerifier
                .create(fileService.delete(testFileId))
                .expectNextMatches(fileEntity -> fileEntity.getId().equals(5L)
                        && fileEntity.getFileName().equals(file1.getFileName())
                        && fileEntity.getFilePath().equals(file1.getFilePath())
                        && fileEntity.getStatus().equals(StatusEntity.DELETED))
                .expectComplete()
                .verify();
    }

    @Test
    void find_All() {
        FileEntity file1 = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .status(StatusEntity.ACTIVE)
                .build();

        FileEntity file2 = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .status(StatusEntity.ACTIVE)
                .build();

        when(fileRepository.findAll()).thenReturn(Flux.just(file1, file2));
        //then
        StepVerifier
                .create(fileService.findAll())
                .expectNext(mapper.map(file1))
                .expectNext(mapper.map(file2))
                .verifyComplete();
    }
}