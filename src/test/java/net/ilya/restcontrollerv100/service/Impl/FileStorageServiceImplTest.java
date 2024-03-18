package net.ilya.restcontrollerv100.service.Impl;

import lombok.SneakyThrows;
import net.ilya.restcontrollerv100.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class FileStorageServiceImplTest {
    private Path root;
    @SpyBean
    FileStorageService fileStorageService;

    @BeforeEach
    public void setup() {
        fileStorageService = new FileStorageServiceImpl();
        root = Path.of("src/test/resources/uploads");
    }

    @SneakyThrows
    @Test
    void init_folder() {
        root = Path.of(root + "/" + "newFolder" + Math.round(Math.random() * 152));
        ReflectionTestUtils.setField(fileStorageService, "root", root);
        fileStorageService.init();
        assertTrue(root.toFile().exists());

    }

    @Test
    void save_file_to_uploads() {
        String test = "testFileName";
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("testFileName");
        ReflectionTestUtils.setField(fileStorageService, "root", root);
        Path resolve = root.resolve(test);
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.just(resolve).then());
        StepVerifier
                .create(fileStorageService.save(Mono.just(filePart)))
                .expectNextMatches(file -> file.equals(test))
                .expectComplete()
                .verify();
    }


}