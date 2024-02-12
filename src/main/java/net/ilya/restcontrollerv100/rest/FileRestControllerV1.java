package net.ilya.restcontrollerv100.rest;

import lombok.RequiredArgsConstructor;
import net.ilya.restcontrollerv100.dto.FileDto;
import net.ilya.restcontrollerv100.mapper.FileMapper;
import net.ilya.restcontrollerv100.service.FileService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class FileRestControllerV1 {

    private final FileService fileService;
    private final FileMapper fileMapper;

    @GetMapping("/api/v1/files/{id}")
    public Mono<FileDto> getFileById(@PathVariable @Validated Long id) {
        return fileService.findById(id).map(fileMapper::map);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PostMapping("/api/v1/files/")
    public Mono<FileDto> createNewFile(@RequestBody @Validated FileDto dto, @Validated MultipartFile file){
        return fileService.create(fileMapper.map(dto),file).map(fileMapper::map);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PutMapping("/api/v1/files/")
    public Mono<FileDto> updateFile(@RequestBody @Validated FileDto dto){
        return fileService.update(fileMapper.map(dto)).map(fileMapper::map);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PutMapping("/api/v1/files/{id}")
    public Mono<FileDto> deletedFile(@RequestParam @Validated Long id){
        return fileService.delete(id).map(fileMapper::map);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/api/v1/files/")
    public Flux<FileDto> getFiles(){
        return fileService.findAll();
    }
}
