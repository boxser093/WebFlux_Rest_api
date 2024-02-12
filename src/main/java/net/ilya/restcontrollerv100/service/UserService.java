package net.ilya.restcontrollerv100.service;

import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.UserEntity;
import org.springframework.security.core.userdetails.User;
import reactor.core.publisher.Mono;

public interface UserService extends GenericService<UserEntity, Long, UserDto> {
    Mono<UserEntity> registerUser(UserEntity user);
    Mono<UserEntity> getUserByUsername(String username);

}
