package net.ilya.restcontrollerv100.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.FileDto;
import net.ilya.restcontrollerv100.mapper.FileMapper;
import net.ilya.restcontrollerv100.security.SecurityService;
import net.ilya.restcontrollerv100.service.FileService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@Tag(name = "File controller", description = "Операция с файлами")
public class FileRestControllerV1 {
    private final FileService fileService;
    private final FileMapper fileMapper;
    private final SecurityService securityService;

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Получить информацию файла по его идентификатору",
            description = "Вы можете получить информацию о загруженном файле по его индетификатору")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/{id}")
    public Mono<FileDto> getFileById(@PathVariable @Validated
                                     @Parameter(description = "Идетификатор файла", in = ParameterIn.PATH,
                                             name = "id",
                                             required = true,
                                             schema = @Schema(
                                                     defaultValue = "1",
                                                     minimum = "1",
                                                     allOf = {Integer.class}
                                             ), style = ParameterStyle.SIMPLE
                                     )
                                     Long id) {
        return fileService.findById(id).map(fileMapper::map);
    }

    @Operation(summary = "Загрузить новый файл в систему",
            description = "Вы можете загрузить новый фаил ")
    @PostMapping("/")
    public Mono<FileDto> createNewFile(@RequestHeader(name = "Authorization") String token,
                                       @RequestPart("file") Mono<FilePart> filePartMono) {
        log.info("JWT TOKEN input {}", filePartMono);
        return fileService.create(securityService.getClaimsFromToken(token), filePartMono).map(fileMapper::map);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get info for user and files",
            description = "You can get a info of u account")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PutMapping("/")
    public Mono<FileDto> updateFile(@RequestBody @Validated FileDto dto) {
        return fileService.update(fileMapper.map(dto)).map(fileMapper::map);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Удалить загруженный фаил",
            description = "Вы можете совершить мягкое удаление информации о загруженном файле из системы")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @DeleteMapping("/{id}")
    public Mono<FileDto> deletedFile(@PathVariable @Validated
                                     @Parameter(description = "Идетификатор файла", in = ParameterIn.PATH,
                                             name = "id",
                                             required = true,
                                             schema = @Schema(
                                                     defaultValue = "1",
                                                     minimum = "1",
                                                     allOf = {Integer.class}
                                             ), style = ParameterStyle.SIMPLE
                                     ) Long id) {
        return fileService.delete(id).map(fileMapper::map);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Получить список загруженных файлов",
            description = "Вы можете получить весь список ранее загру")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/")
    public Flux<FileDto> getFiles() {
        return fileService.findAll();
    }
}
