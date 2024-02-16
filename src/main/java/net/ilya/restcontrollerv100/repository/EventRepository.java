package net.ilya.restcontrollerv100.repository;

import net.ilya.restcontrollerv100.entity.EventEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;


public interface EventRepository extends R2dbcRepository<EventEntity, Long> {
}
