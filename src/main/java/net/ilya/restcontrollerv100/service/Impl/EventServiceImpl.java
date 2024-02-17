package net.ilya.restcontrollerv100.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.EventEntity;
import net.ilya.restcontrollerv100.entity.FileEntity;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.entity.UserEntity;
import net.ilya.restcontrollerv100.mapper.EventMapper;
import net.ilya.restcontrollerv100.repository.EventRepository;
import net.ilya.restcontrollerv100.repository.FileRepository;
import net.ilya.restcontrollerv100.repository.UserRepository;
import net.ilya.restcontrollerv100.service.EventService;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper mapper;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Override
    public Mono<EventEntity> findById(Long aLong) {
        return eventRepository.findById(aLong);
    }

    @Override
    public Mono<EventEntity> create(EventEntity eventEntity) {
        EventEntity build = eventEntity.toBuilder()
                .status(StatusEntity.ACTIVE)
                .fileId(eventEntity.getFileEntity().getId())
                .userId(eventEntity.getUserEntity().getId())
                .build();
        log.info("IN EventServiceImpl create {}", build);
        return eventRepository.save(build);
    }

    @Override
    public Mono<EventEntity> update(EventEntity eventEntity) {
        log.info("IN EventRepository update {}", eventEntity);
        return eventRepository.findById(eventEntity.getId())
                .map(eventEntity1 -> eventEntity1.toBuilder()
                        .status(StatusEntity.UPGRADE)
                        .fileId(eventEntity.getFileEntity().getId())
                        .userId(eventEntity.getUserEntity().getId())
                        .build())
                .flatMap(eventRepository::save);
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
