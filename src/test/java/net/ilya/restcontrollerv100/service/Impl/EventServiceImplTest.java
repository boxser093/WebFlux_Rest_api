package net.ilya.restcontrollerv100.service.Impl;

import net.ilya.restcontrollerv100.entity.*;
import net.ilya.restcontrollerv100.mapper.EventMapper;
import net.ilya.restcontrollerv100.repository.EventRepository;
import net.ilya.restcontrollerv100.repository.FileRepository;
import net.ilya.restcontrollerv100.repository.UserRepository;
import net.ilya.restcontrollerv100.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest
class EventServiceImplTest {
    @MockBean
    EventRepository eventRepository;

    @SpyBean
    EventMapper eventMapper;
    @SpyBean
    EventService eventService;

    @BeforeEach
    public void setup() {
        eventService = new EventServiceImpl(eventRepository, eventMapper);
    }
    @Test
    void find_EventEntity_by_id() {
        EventEntity event = EventEntity.builder()
                .id(1L)
                .userId(2L)
                .fileId(33L)
                .userEntity(null)
                .fileEntity(null)
                .build();

        long eventId = 1L;

        //when
        when(eventRepository.findById(eventId)).thenReturn(Mono.just(event));
        //then
        Mono<EventEntity> entityMono = eventService.findById(1L);
        StepVerifier
                .create(entityMono)
                .expectNextMatches(eventEntity -> eventEntity.getUserId().equals(2L)
                        && eventEntity.getFileId().equals(33L))
                .expectComplete()
                .verify();
    }
    @Test
    void create_Event() {
        EventEntity event = EventEntity.builder()
                .userId(22L)
                .fileId(31L)
                .userEntity(null)
                .fileEntity(null)
                .build();

        EventEntity beforeSaveEvent = EventEntity.builder()
                .id(12L)
                .userId(22L)
                .fileId(31L)
                .status(StatusEntity.ACTIVE)
                .userEntity(null)
                .fileEntity(null)
                .build();
        long eventId = 12L;
        //when
        when(eventRepository.save(event.toBuilder()
                .status(StatusEntity.ACTIVE)
                .build()))
                .thenReturn(Mono.just(beforeSaveEvent));

        //then
        Mono<EventEntity> entityMono = eventService.create(event);

        StepVerifier
                .create(entityMono)
                .expectNextMatches(eventEntity ->
                        eventEntity.getId().equals(eventId)
                                && eventEntity.getFileId().equals(31L)
                                && eventEntity.getUserId().equals(22L)
                                && eventEntity.getStatus().equals(StatusEntity.ACTIVE))
                .expectComplete()
                .verify();
    }

    @Test
    void update_Event() {
        UserEntity oldUser = UserEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .firstName("Gabe" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Gabenov" + Math.round(Math.random() + (Math.random() * 999)))
                .username("test" + Math.round(Math.random() + (Math.random() * 999)))
                .password("test" + Math.round(Math.random() + (Math.random() * 999)))
                .role(UserRole.USER)
                .build();

        FileEntity oldFile = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .build();

        UserEntity newUser = UserEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .firstName("Jimmy" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Cho" + Math.round(Math.random() + (Math.random() * 999)))
                .username("test" + Math.round(Math.random() + (Math.random() * 999)))
                .password("test" + Math.round(Math.random() + (Math.random() * 999)))
                .role(UserRole.USER)
                .build();

        FileEntity newFile = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName("imageSonya.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .build();


        long eventTestId = 1L;

        EventEntity beforeUpdateEvent = EventEntity.builder()
                .id(eventTestId)
                .userId(oldUser.getId())
                .fileId(oldFile.getId())
                .userEntity(oldUser)
                .fileEntity(oldFile)
                .status(StatusEntity.ACTIVE)
                .build();

        EventEntity toUpdateEvent = EventEntity.builder()
                .id(eventTestId)
                .userId(newUser.getId())
                .fileId(newFile.getId())
                .userEntity(newUser)
                .fileEntity(newFile)
                .status(StatusEntity.UPGRADE)
                .build();

        //when
        when(eventRepository.findById(eventTestId)).thenReturn(Mono.just(beforeUpdateEvent));
        when(eventRepository.save(toUpdateEvent)).thenReturn(Mono.just(toUpdateEvent));
        //then
        Mono<EventEntity> updateMono = eventService.update(toUpdateEvent.toBuilder()
                .status(StatusEntity.ACTIVE)
                .build());

        StepVerifier
                .create(updateMono)
                .expectNextMatches(eventEntity ->
                        eventEntity.getUserId().equals(newUser.getId())
                                && eventEntity.getFileId().equals(newFile.getId())
                                && eventEntity.getStatus().equals(StatusEntity.UPGRADE))
                .expectComplete()
                .verify();
    }

    @Test
    void soft_delete_Event() {
        EventEntity event = EventEntity.builder()
                .id(1L)
                .userId(2L)
                .fileId(33L)
                .userEntity(null)
                .fileEntity(null)
                .build();

        long eventId = 1L;

        //when
        when(eventRepository.findById(eventId)).thenReturn(Mono.just(event));
        when(eventRepository.save(event.toBuilder()
                .status(StatusEntity.DELETED)
                .build())).thenReturn(Mono.just(event.toBuilder()
                .status(StatusEntity.DELETED)
                .build()));
        //then
        Mono<EventEntity> entityMono = eventService.delete(1L);
        StepVerifier
                .create(entityMono)
                .expectNextMatches(eventEntity -> eventEntity.getUserId().equals(2L)
                        && eventEntity.getFileId().equals(33L)
                        && eventEntity.getStatus().equals(StatusEntity.DELETED))
                .expectComplete()
                .verify();
    }

    @Test
    void find_All_Events() {

        UserEntity user1 = UserEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .firstName("Gabe" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Gabenov" + Math.round(Math.random() + (Math.random() * 999)))
                .username("test" + Math.round(Math.random() + (Math.random() * 999)))
                .password("test" + Math.round(Math.random() + (Math.random() * 999)))
                .role(UserRole.USER)
                .build();

        FileEntity file1 = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .firstName("Jimmy" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Cho" + Math.round(Math.random() + (Math.random() * 999)))
                .username("test" + Math.round(Math.random() + (Math.random() * 999)))
                .password("test" + Math.round(Math.random() + (Math.random() * 999)))
                .role(UserRole.USER)
                .build();

        FileEntity file2 = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName("imageSonya.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .build();


        long eventTestId = 1L;

        EventEntity event1 = EventEntity.builder()
                .id(eventTestId)
                .userId(user1.getId())
                .fileId(file1.getId())
                .userEntity(user1)
                .fileEntity(file1)
                .status(StatusEntity.ACTIVE)
                .build();

        EventEntity event2 = EventEntity.builder()
                .id(eventTestId)
                .userId(user2.getId())
                .fileId(file2.getId())
                .userEntity(user2)
                .fileEntity(file2)
                .status(StatusEntity.ACTIVE)
                .build();

        when(eventRepository.findAll()).thenReturn(Flux.just(event1, event2));

        StepVerifier
                .create(eventService.findAll())
                .expectNext(eventMapper.map(event1))
                .expectNext(eventMapper.map(event2))
                .verifyComplete();
    }
}