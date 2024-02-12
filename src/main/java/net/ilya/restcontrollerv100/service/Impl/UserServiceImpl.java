package net.ilya.restcontrollerv100.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.entity.UserEntity;
import net.ilya.restcontrollerv100.entity.UserRole;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.repository.UserRepository;
import net.ilya.restcontrollerv100.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Override
    public Mono<UserEntity> findById(Long aLong) {
        log.info("IN UserServiceImpl findById {}", aLong);
        return userRepository.findById(aLong);
    }

    @Override
    public Mono<UserEntity> create(UserEntity user) {
        return userRepository.save(user.toBuilder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(user.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
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
                .map(user -> UserEntity.builder()
                        .status(StatusEntity.DELETED)
                        .updatedAt(LocalDateTime.now())
                        .build())
                .flatMap(userRepository::save).doOnSuccess(
                        user -> {
                            log.info("IN UserServiceImpl - user: {} deleted", user);
                        });
    }

    @Override
    public Flux<UserDto> findAll() {
        log.info("IN UserServiceImpl findAll");
        Flux<UserEntity> all = userRepository.findAll();
        Flux<UserDto> result = Flux.from(all.map(mapper::map));
        return result;
    }

    @Override
    public Mono<UserEntity> registerUser(UserEntity user) {
        return userRepository.save(
                user.toBuilder()
                        .password(passwordEncoder.encode(user.getPassword()))
                        .role(UserRole.USER)
                        .status(StatusEntity.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        ).doOnSuccess(u -> {
            log.info("IN registerUser - user: {} created", u);
        });
    }

    @Override
    public Mono<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
