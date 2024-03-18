package net.ilya.restcontrollerv100.service;

import io.jsonwebtoken.Claims;
import net.ilya.restcontrollerv100.dto.FileDto;
import net.ilya.restcontrollerv100.entity.FileEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;


public interface FileService extends GenericService<FileEntity, Long, FileDto> {
    Mono<FileEntity> create(Claims userClaim, Mono<FilePart> filePartMono);

}
