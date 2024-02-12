package net.ilya.restcontrollerv100.repository;

import net.ilya.restcontrollerv100.entity.FileEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface FileRepository extends R2dbcRepository<FileEntity, Long> {
//    @Query("SELECT * FROM files WHERE status not like 'DELETED'")
//    Flux<FileEntity> findAllByFileStatus_DeletedNot();
}
