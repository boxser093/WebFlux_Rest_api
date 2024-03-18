package net.ilya.restcontrollerv100.service.Impl;

import net.ilya.restcontrollerv100.entity.*;
import net.ilya.restcontrollerv100.mapper.UserMapper;
import net.ilya.restcontrollerv100.repository.EventRepository;
import net.ilya.restcontrollerv100.repository.FileRepository;
import net.ilya.restcontrollerv100.repository.UserRepository;
import net.ilya.restcontrollerv100.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceImplTest {

    @MockBean
    EventRepository eventRepository;
    @MockBean
    FileRepository fileRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    PasswordEncoder passwordEncoder;
    @SpyBean
    UserService userService;
    @SpyBean
    UserMapper mapper;

    @BeforeEach
    public void setup() {
        userService = new UserServiceImpl(eventRepository, fileRepository, userRepository, passwordEncoder, mapper);
    }

    @Test
    void findById() {
        long testUserId = 5L;
        UserEntity userEntity = UserEntity.builder()
                .id(5L)
                .firstName("Gabe" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Gabenov" + Math.round(Math.random() + (Math.random() * 999)))
                .username("test" + Math.round(Math.random() + (Math.random() * 999)))
                .password(passwordEncoder.encode("test" + Math.round(Math.random() + (Math.random() * 999))))
                .role(UserRole.USER)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Mono.just(userEntity));
        //then
        StepVerifier
                .create(userService.findById(testUserId))
                .expectNextMatches(userEntity1 -> userEntity1.getId().equals(5L)
                        && userEntity.getUsername().equals(userEntity.getUsername()))
                .expectComplete()
                .verify();
    }

    @Test
    void find_By_Id_With_Events() {
        long testUserId = 5L;

        FileEntity file1 = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .status(StatusEntity.ACTIVE)
                .build();

        FileEntity file2 = FileEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .fileName("imageGaben.image" + Math.round(Math.random() + (Math.random() * 999)))
                .filePath("somePath/path/path" + Math.round(Math.random() + (Math.random() * 999)))
                .status(StatusEntity.ACTIVE)
                .build();

        EventEntity event = EventEntity.builder()
                .id(1L)
                .status(StatusEntity.ACTIVE)
                .userId(testUserId)
                .fileId(file1.getId())
                .userEntity(null)
                .fileEntity(file1)
                .build();

        EventEntity event2 = EventEntity.builder()
                .id(2L)
                .status(StatusEntity.ACTIVE)
                .userId(testUserId)
                .fileId(file2.getId())
                .userEntity(null)
                .fileEntity(file2)
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id(5L)
                .firstName("Gabe" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Gabenov" + Math.round(Math.random() + (Math.random() * 999)))
                .username("test" + Math.round(Math.random() + (Math.random() * 999)))
                .password(passwordEncoder.encode("test" + Math.round(Math.random() + (Math.random() * 999))))
                .role(UserRole.USER)
                .status(StatusEntity.ACTIVE)
                .eventEntityList(List.of(event, event2))
                .build();
        when(eventRepository.findAll()).thenReturn(Flux.just(event, event2));
        when(fileRepository.findAll()).thenReturn(Flux.just(file1, file2));
        when(userRepository.findById(testUserId)).thenReturn(Mono.just(userEntity));
        //then
        StepVerifier
                .create(userService.findByIdWithEvents(testUserId))
                .expectNextMatches(user -> user.getId().equals(userEntity.getId())
                        && user.getUsername().equals(userEntity.getUsername()) &&
                        user.getEventEntityList().containsAll(List.of(event, event2)))
                .expectComplete()
                .verify();
    }

    @Test
    void create_UserEntity() {
        UserEntity userEntity = UserEntity.builder()
                .firstName("Guts" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Berserk" + Math.round(Math.random() + (Math.random() * 999)))
                .username("guts" + Math.round(Math.random() + (Math.random() * 999)))
                .password("bigSword" + Math.round(Math.random() + (Math.random() * 999)))
                .build();

        UserEntity inService = UserEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername())
                .password("encodePassword")
                .role(UserRole.USER)
                .status(StatusEntity.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(passwordEncoder.encode(userEntity.getPassword())).thenReturn("encodePassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(inService));

        StepVerifier
                .create(userService.create(userEntity))
                .expectNextMatches(createdUser ->
                        createdUser.getRole().equals(UserRole.USER)
                                && createdUser.getStatus().equals(StatusEntity.ACTIVE)
                                && createdUser.getPassword().equals("encodePassword"))
                .expectComplete()
                .verify();
    }

    @Test
    void update_User() {

        UserEntity beforeUpdate = UserEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .firstName("Guts" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Berserk" + Math.round(Math.random() + (Math.random() * 999)))
                .username("guts" + Math.round(Math.random() + (Math.random() * 999)))
                .password("encodePassword")
                .role(UserRole.USER)
                .status(StatusEntity.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserEntity afterUpdate = UserEntity.builder()
                .id(beforeUpdate.getId())
                .firstName("Griffith" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("White Falcon" + Math.round(Math.random() + (Math.random() * 999)))
                .username("griffith" + Math.round(Math.random() + (Math.random() * 999)))
                .password("encodePassword")
                .role(UserRole.USER)
                .status(StatusEntity.UPGRADE)
                .updatedAt(LocalDateTime.now())
                .build();
        when(userRepository.findById(beforeUpdate.getId())).thenReturn(Mono.just(beforeUpdate));
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(afterUpdate));
        StepVerifier
                .create(userService.update(beforeUpdate))
                .expectNextMatches(user -> user.getId().equals(beforeUpdate.getId())
                        && user.getFirstName().equals(afterUpdate.getFirstName())
                        && user.getStatus().equals(StatusEntity.UPGRADE))
                .expectComplete()
                .verify();
    }

    @Test
    void soft_delete_User() {

        long testUserId = 5L;
        UserEntity userEntity = UserEntity.builder()
                .id(5L)
                .firstName("Gabe" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Gabenov" + Math.round(Math.random() + (Math.random() * 999)))
                .username("test" + Math.round(Math.random() + (Math.random() * 999)))
                .password(passwordEncoder.encode("test" + Math.round(Math.random() + (Math.random() * 999))))
                .role(UserRole.USER)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Mono.just(userEntity));
        when(userRepository.save(userEntity.toBuilder()
                .status(StatusEntity.DELETED)
                .build())).thenReturn(Mono.just(userEntity.toBuilder()
                .status(StatusEntity.DELETED)
                .build()));
        //then
        StepVerifier
                .create(userService.delete(testUserId))
                .expectNextMatches(userEntity1 -> userEntity1.getId().equals(5L)
                        && userEntity1.getUsername().equals(userEntity.getUsername())
                        && userEntity1.getStatus().equals(StatusEntity.DELETED))
                .expectComplete()
                .verify();

    }

    @Test
    void find_All_Users() {
        UserEntity userEntity1 = UserEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .firstName("User" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("UserLastName" + Math.round(Math.random() + (Math.random() * 999)))
                .username("Username" + Math.round(Math.random() + (Math.random() * 999)))
                .password(passwordEncoder.encode("test" + Math.round(Math.random() + (Math.random() * 999))))
                .role(UserRole.USER)
                .build();

        UserEntity userEntity2 = UserEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .firstName("User" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("UserLastName" + Math.round(Math.random() + (Math.random() * 999)))
                .username("Username" + Math.round(Math.random() + (Math.random() * 999)))
                .password(passwordEncoder.encode("test" + Math.round(Math.random() + (Math.random() * 999))))
                .role(UserRole.USER)
                .build();

        when(userRepository.findAll()).thenReturn(Flux.just(userEntity1, userEntity2));

        StepVerifier
                .create(userService.findAll())
                .expectNext(mapper.map(userEntity1))
                .expectNext(mapper.map(userEntity2))
                .verifyComplete();

    }

    @Test
    void register_User() {

        UserEntity userEntity = UserEntity.builder()
                .firstName("Guts" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("Berserk" + Math.round(Math.random() + (Math.random() * 999)))
                .username("guts" + Math.round(Math.random() + (Math.random() * 999)))
                .password("bigSword" + Math.round(Math.random() + (Math.random() * 999)))
                .build();

        UserEntity inService = UserEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername())
                .password("encodePassword")
                .role(UserRole.USER)
                .status(StatusEntity.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(passwordEncoder.encode(userEntity.getPassword())).thenReturn("encodePassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(inService));

        StepVerifier
                .create(userService.registerUser(userEntity))
                .expectNextMatches(createdUser ->
                        createdUser.getRole().equals(UserRole.USER)
                                && createdUser.getStatus().equals(StatusEntity.ACTIVE)
                                && createdUser.getPassword().equals("encodePassword"))
                .expectComplete()
                .verify();
    }

    @Test
    void get_User_By_Username() {

        UserEntity userEntity1 = UserEntity.builder()
                .id(Math.round(Math.random() + (Math.random() * 99)))
                .firstName("User" + Math.round(Math.random() + (Math.random() * 999)))
                .lastName("UserLastName" + Math.round(Math.random() + (Math.random() * 999)))
                .username("Username" + Math.round(Math.random() + (Math.random() * 999)))
                .password(passwordEncoder.encode("test" + Math.round(Math.random() + (Math.random() * 999))))
                .role(UserRole.USER)
                .build();
        String findUserName = userEntity1.getUsername();

        when(userRepository.findUserEntitiesByUsername(findUserName)).thenReturn(Mono.just(userEntity1));

        StepVerifier
                .create(userService.getUserByUsername(findUserName))
                .expectNextMatches(userEntityFromDB ->
                        userEntityFromDB.getUsername().equals(userEntity1.getUsername())
                                && userEntityFromDB.getId().equals(userEntity1.getId())
                ).verifyComplete();

    }
}