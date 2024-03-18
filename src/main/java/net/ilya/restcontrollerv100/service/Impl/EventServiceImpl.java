package net.ilya.restcontrollerv100.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.entity.EventEntity;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.mapper.EventMapper;
import net.ilya.restcontrollerv100.repository.EventRepository;
import net.ilya.restcontrollerv100.repository.FileRepository;
import net.ilya.restcontrollerv100.repository.UserRepository;
import net.ilya.restcontrollerv100.service.EventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper mapper;


    @Override
    public Mono<EventEntity> findById(Long aLong) {
        return eventRepository.findById(aLong);
    }

    @Override
    public Mono<EventEntity> create(EventEntity eventEntity) {
        EventEntity build = eventEntity.toBuilder()
                .status(StatusEntity.ACTIVE)
                .build();
        log.info("IN EventServiceImpl create {}", build);
        return eventRepository.save(build);
    }

    @Override
    public Mono<EventEntity> update(EventEntity eventEntity) {
        log.info("IN EventRepository to update {}", eventEntity);

        Mono<EventEntity> entityMono = eventRepository.findById(eventEntity.getId())
                .map(eventEntity1 -> {
                    log.info("IN EventRepository before update {}", eventEntity1);
                    EventEntity build = eventEntity1.toBuilder()
                            .status(StatusEntity.UPGRADE)
                            .fileId(eventEntity.getFileId())
                            .userId(eventEntity.getUserId())
                            .userEntity(eventEntity.getUserEntity())
                            .fileEntity(eventEntity.getFileEntity())
                            .build();
                    log.info("IN EventRepository after update {}", build);
                    return build;
                })
                .flatMap(eventRepository::save);
        return entityMono;
    }

    @Override
    public Mono<EventEntity> delete(Long aLong) {
        log.info("IN EventRepository delete {}", aLong);
        return eventRepository.findById(aLong)
                .map(eventEntity -> eventEntity.toBuilder()
                        .status(StatusEntity.DELETED)
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
