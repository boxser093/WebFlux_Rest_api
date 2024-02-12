package net.ilya.restcontrollerv100.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.EventEntity;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.mapper.EventMapper;
import net.ilya.restcontrollerv100.repository.EventRepository;
import net.ilya.restcontrollerv100.service.EventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper mapper;
    @Override
    public Mono<EventEntity> findById(Long aLong) {
        log.info("IN EventRepository findById {}", aLong);
        return eventRepository.findById(aLong);
    }

    @Override
    public Mono<EventEntity> create(EventEntity eventEntity) {
        log.info("IN EventRepository create {}", eventEntity);
        return eventRepository.save(
                eventEntity.toBuilder()
                        .eventStatus(StatusEntity.ACTIVE)
                        .build());
    }

    @Override
    public Mono<EventEntity> update(EventEntity eventEntity) {
        log.info("IN EventRepository update {}", eventEntity);
        return eventRepository.findById(eventEntity.getId())
                .map(eventEntity1 -> eventEntity1.toBuilder()
                        .eventStatus(StatusEntity.UPGRADE)
                        .file(eventEntity.getFile())
                        .user(eventEntity.getUser())
                        .build())
                .flatMap(eventRepository::save);
    }

    @Override
    public Mono<EventEntity> delete(Long aLong) {
        log.info("IN EventRepository delete {}", aLong);
        return eventRepository.findById(aLong)
                .map(eventEntity -> eventEntity.toBuilder()
                        .eventStatus(StatusEntity.DELETED)
                        .build())
                .flatMap(eventRepository::save);
    }

    @Override
    public Flux<EventDto> findAll() {
        log.info("IN EventRepository findAll");
        Flux<EventEntity> all = eventRepository.findAll();
        return Flux.from(all.map(mapper::map));
    }
}
