package net.ilya.restcontrollerv100.service;

import io.jsonwebtoken.Claims;
import net.ilya.restcontrollerv100.dto.FileDto;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.FileEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.List;

public interface FileService extends GenericService<FileEntity, Long, FileDto> {
    Mono<FileEntity> create(Claims userClaim, Mono<FilePart> filePartMono);

}
