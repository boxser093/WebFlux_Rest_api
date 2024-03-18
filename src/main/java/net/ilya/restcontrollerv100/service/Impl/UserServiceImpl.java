package net.ilya.restcontrollerv100.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.*;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.repository.EventRepository;
import net.ilya.restcontrollerv100.repository.FileRepository;
import net.ilya.restcontrollerv100.repository.UserRepository;
import net.ilya.restcontrollerv100.service.UserService;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final EventRepository eventRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserMapper mapper;

    @Override
    public Mono<UserEntity> findById(Long aLong) {
        log.info("IN UserServiceImpl findById {}", aLong);
        return userRepository.findById(aLong);
    }

    public Mono<UserEntity> findByIdWithEvents(Long aLong) {
        Mono<List<EventEntity>> listEvents = eventRepository.findAll().filter(x -> x.getUserId().equals(aLong) && !x.getStatus().equals(StatusEntity.DELETED)).collectList();
        Mono<List<FileEntity>> listFiles = fileRepository.findAll().filter(x -> !x.getStatus().equals(StatusEntity.DELETED)).collectList();
        Mono<UserEntity> userEntityMono = userRepository.findById(aLong);
        return Mono.zip(listEvents, listFiles, userEntityMono).flatMap(t -> {
            List<EventEntity> t1 = t.getT1();
            List<FileEntity> t2 = t.getT2();
            UserEntity t3 = t.getT3();
            for (EventEntity eventEntity : t1) {
                for (FileEntity fileEntity : t2) {
                    if (eventEntity.getFileId().equals(fileEntity.getId())) {
                        eventEntity.setFileEntity(fileEntity);
                        eventEntity.setUserEntity(t3);
                    }
                }
            }
            return Mono.just(t3.toBuilder()
                    .eventEntityList(t1)
                    .build());
        });
    }


    @Override
    public Mono<UserEntity> create(UserEntity user) {
        return userRepository.save(user.toBuilder()
                .password(passwordEncoder.encode(user.getPassword()))
                .status(StatusEntity.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        ).doOnSuccess(u -> {
            log.info("IN create - user: {} created", u);
        });
    }

    @Override
    public Mono<UserEntity> update(UserEntity user) {
        log.info("IN UserServiceImpl update {}", user);

        return userRepository.findById(user.getId())
                .map(user1 -> user1.toBuilder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .role(user.getRole())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .status(StatusEntity.UPGRADE)
                        .updatedAt(LocalDateTime.now())
                        .build())
                .flatMap(userRepository::save);
    }

    @Override
    public Mono<UserEntity> delete(Long aLong) {
        log.info("IN UserServiceImpl delete {}", aLong);
        return userRepository.findById(aLong)
                .map(user -> {
                    user.setStatus(StatusEntity.DELETED);
                    return user;
                })
                .flatMap(userRepository::save).doOnSuccess(
                        user -> {
                            log.info("IN UserServiceImpl - user: {} deleted", user);
                        });
    }

    @Override
    public Flux<UserDto> findAll() {
        log.info("IN UserServiceImpl findAll");
        return Flux.from(userRepository.findAll().map(mapper::map));
    }

    @Override
     public Mono<UserEntity> registerUser(UserEntity user) {
        UserEntity build = user.toBuilder()
                .password(passwordEncoder.encode(user.getPassword()))
                .role(UserRole.USER)
                .status(StatusEntity.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        log.info("IN UserServiceImpl in register - {}", build);
        return userRepository.save(build);
    }

    @Override
    public Mono<UserEntity> getUserByUsername(String username) {
        return userRepository.findUserEntitiesByUsername(username);
    }
}
