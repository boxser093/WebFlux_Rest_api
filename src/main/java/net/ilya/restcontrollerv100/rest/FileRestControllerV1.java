package net.ilya.restcontrollerv100.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.FileDto;
import net.ilya.restcontrollerv100.dto.FileUpload;
import net.ilya.restcontrollerv100.mapper.FileMapper;
import net.ilya.restcontrollerv100.security.SecurityService;
import net.ilya.restcontrollerv100.service.FileService;
import net.ilya.restcontrollerv100.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileRestControllerV1 {
    private final FileService fileService;
    private final FileMapper fileMapper;
    private final SecurityService securityService;
    private final FileStorageService fileStorageService;


    @GetMapping("/api/v1/files/{id}")
    public Mono<FileDto> getFileById(@PathVariable @Validated Long id) {
        return fileService.findById(id).map(fileMapper::map);
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PostMapping("/api/v1/files/")
    public Mono<FileDto> createNewFile(@RequestHeader (name="Authorization") String token, @RequestPart("file") Mono<FilePart> filePartMono) {
        log.info("JWT TOKEN input {}", filePartMono);
        return fileService.create(securityService.getClaimsFromToken(token), filePartMono).map(fileMapper::map);
    }
    @PostMapping("/api/v1/files/test")
    public Mono<ResponseEntity<FileUpload>> uploadFile(@RequestPart("file") Mono<FilePart> filePartMono){
        return fileStorageService.save(filePartMono).map(fileUpload -> ResponseEntity.ok().body(FileUpload.builder().name(fileUpload).build()));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PutMapping("/api/v1/files/")
    public Mono<FileDto> updateFile(@RequestBody @Validated FileDto dto) {
        return fileService.update(fileMapper.map(dto)).map(fileMapper::map);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PutMapping("/api/v1/files/{id}")
    public Mono<FileDto> deletedFile(@RequestParam @Validated Long id) {
        return fileService.delete(id).map(fileMapper::map);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/api/v1/files/")
    public Flux<FileDto> getFiles() {
        return fileService.findAll();
    }
}
