package net.ilya.restcontrollerv100.repository;

import net.ilya.restcontrollerv100.entity.FileEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FileRepository extends R2dbcRepository<FileEntity, Long> {
}
