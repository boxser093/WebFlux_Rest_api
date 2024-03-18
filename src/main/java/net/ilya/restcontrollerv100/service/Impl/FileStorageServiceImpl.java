package net.ilya.restcontrollerv100.service.Impl;

import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${uploads.filePath}")
    private Path root;

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public Mono<String> save(Mono<FilePart> filePartMono) {
        return filePartMono.doOnNext(fp -> System.out.println("Receiving File:" + fp.filename())).flatMap(filePart -> {
            String filename = filePart.filename();
            log.info("IN FileStorageServiceImpl save - {} to {}", filename, root);
            return filePart.transferTo(root.resolve(filename)).then(Mono.just(filename));
        });
    }

}
