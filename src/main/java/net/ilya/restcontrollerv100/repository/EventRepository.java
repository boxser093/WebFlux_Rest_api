package net.ilya.restcontrollerv100.repository;

import net.ilya.restcontrollerv100.entity.EventEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface EventRepository extends R2dbcRepository<EventEntity, Long> {
//    @Query("SELECT * FROM files WHERE status not like 'DELETED'")
//    Flux<EventEntity> findAllByEventStatus_DeletedIsNotLike();
}
