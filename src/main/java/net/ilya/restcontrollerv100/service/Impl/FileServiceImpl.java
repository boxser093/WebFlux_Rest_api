package net.ilya.restcontrollerv100.service.Impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.FileDto;
import net.ilya.restcontrollerv100.entity.EventEntity;
import net.ilya.restcontrollerv100.entity.FileEntity;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.entity.UserEntity;
import net.ilya.restcontrollerv100.mapper.FileMapper;
import net.ilya.restcontrollerv100.repository.FileRepository;
import net.ilya.restcontrollerv100.service.EventService;
import net.ilya.restcontrollerv100.service.FileService;
import net.ilya.restcontrollerv100.service.FileStorageService;
import net.ilya.restcontrollerv100.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileStorageService fileStorageService;
    private final FileRepository fileRepository;
    private final FileMapper mapper;
    private final EventService eventService;
    private final UserService userService;
    private final AmazonS3 amazonS3;

    @Value("${uploads.filePath}")
    private Path filePath;
    @Value("${spring.cloud.aws.buckets.default}")
    private String bucketName;

    @Override
    public Mono<FileEntity> findById(Long aLong) {
        log.info("IN FileServiceImpl fineOneById {}", aLong);
        return fileRepository.findById(aLong);
    }

    @Override
    public Mono<FileEntity> create(FileEntity fileEntity) {
        return fileRepository.save(fileEntity.toBuilder()
                .status(StatusEntity.ACTIVE)
                .build());
    }

    @Override
    public Mono<FileEntity> create(Claims userClaims, Mono<FilePart> filePartMono) {
        Mono<UserEntity> byId = userService.getUserByUsername(userClaims.get("username", String.class));
        Mono<String> saveFile = fileStorageService.save(filePartMono);
        Mono<FileEntity> fileEntityMono = saveFile.flatMap(u -> fileRepository.save(FileEntity.builder()
                .fileName(u)
                .filePath(filePath.toString())
                .status(StatusEntity.ACTIVE)
                .build()));
        Mono<EventEntity> monoEventEmpty = Mono.just(EventEntity.builder().build());
        Mono.zip(byId, fileEntityMono,monoEventEmpty).flatMap(u -> {
            Mono.just(amazonS3.putObject(new PutObjectRequest(bucketName, u.getT2().getFileName(), Path.of(filePath + "/" + u.getT2().getFileName()).toFile())))
                    .subscribe(i -> log.info("FILE UPLOAD TO AMAZON - {}", i.getETag()));
            log.info("$$$ IN FileServiceImpl create  - user ID - {}, file ID - {}",u.getT1().getId(),u.getT2().getId());
            EventEntity build = u.getT3().toBuilder()
                    .userEntity(u.getT1())
                    .fileEntity(u.getT2())
                    .fileId(u.getT2().getId())
                    .userId(u.getT2().getId())
                    .build();
            log.info("$$$ IN FileServiceImpl create EventEntity - {}", build);
            return eventService.create(build);
        });

        return fileEntityMono;
    }

    @Override
    public Mono<FileEntity> update(FileEntity fileEntity) {

        return fileRepository.findById(fileEntity.getId())
                .map(fileEntity1 -> fileEntity1.toBuilder()
                        .status(StatusEntity.UPGRADE)
                        .fileName(fileEntity.getFileName())
                        .filePath(fileEntity.getFilePath())
                        .build())
                .flatMap(fileRepository::save);
    }

    @Override
    public Mono<FileEntity> delete(Long aLong) {
        log.info("IN FileServiceImpl delete {}", aLong);
        return fileRepository.findById(aLong)
                .map(fileEntity -> fileEntity.toBuilder()
                        .status(StatusEntity.DELETED)
                        .build())
                .flatMap(fileRepository::save);
    }


    public Flux<FileDto> findAll() {
        log.info("IN FileServiceImpl findAll");
        Flux<FileEntity> all = fileRepository.findAll();
        return Flux.from(all.map(mapper::map));
    }
}
