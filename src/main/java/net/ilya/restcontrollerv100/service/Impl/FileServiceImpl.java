package net.ilya.restcontrollerv100.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.FileDto;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.FileEntity;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.mapper.FileMapper;
import net.ilya.restcontrollerv100.repository.FileRepository;
import net.ilya.restcontrollerv100.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final FileMapper mapper;
    @Value("${uploads.fileMaxSize}")
    private int fileMaxSize;
    @Value("${uploads.memMaxSize}")
    private int memMaxSize;
    @Value("${uploads.filePath}")
    private Path filePath;

    private void init() {
        try {
            Files.createDirectories(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }
    private Path downloadFileToStorage(MultipartFile file){
        Path fullResource;
        try {
            fullResource = Path.of(String.valueOf(this.filePath.resolve(file.getOriginalFilename())));
            Files.copy(file.getInputStream(), this.filePath.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
        return fullResource;
    }
    @Override
    public Mono<FileEntity> findById(Long aLong) {
        log.info("IN FileServiceImpl fineOneById {}", aLong);
        return fileRepository.findById(aLong);
    }

    @Override
    public Mono<FileEntity> create(FileEntity fileEntity) {
        return null;
    }


    @Override
    public Mono<FileEntity> create(FileEntity fileEntity, MultipartFile file) {
        log.info("IN FileServiceImpl save {}", fileEntity);
        Path path = downloadFileToStorage(file);
        return fileRepository.save(
                fileEntity.toBuilder()
                        .fileName(String.valueOf(path.getFileName()))
                        .fileStatus(StatusEntity.ACTIVE)
                        .build()
        );
    }
    @Override
    public Mono<FileEntity> update(FileEntity fileEntity) {

        return fileRepository.findById(fileEntity.getId())
                .map(fileEntity1 -> fileEntity1.toBuilder()
                        .fileStatus(StatusEntity.UPGRADE)
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
                        .fileStatus(StatusEntity.DELETED)
                        .build())
                .flatMap(fileRepository::save);
    }


    public Flux<FileDto> findAll() {
        log.info("IN FileServiceImpl findAll");
        Flux<FileEntity> all = fileRepository.findAll();
        return Flux.from(all.map(mapper::map));
    }
}
