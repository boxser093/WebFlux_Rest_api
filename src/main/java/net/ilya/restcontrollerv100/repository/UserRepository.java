package net.ilya.restcontrollerv100.repository;

import net.ilya.restcontrollerv100.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {
    @Query("SELECT * FROM users WHERE username = :username")
    Mono<UserEntity> findUserEntitiesByUsername(String username);
}
