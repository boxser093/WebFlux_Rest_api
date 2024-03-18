package net.ilya.restcontrollerv100.service;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.stream.Stream;

@Service
public interface FileStorageService {
    public void init();
    public Mono<String> save(Mono<FilePart> filePartMono);


}
