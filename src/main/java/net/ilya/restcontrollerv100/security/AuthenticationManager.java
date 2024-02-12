package net.ilya.restcontrollerv100.security;

import lombok.RequiredArgsConstructor;
import net.ilya.restcontrollerv100.entity.StatusEntity;
import net.ilya.restcontrollerv100.entity.UserEntity;
import net.ilya.restcontrollerv100.exeption.UnauthorizedException;
import net.ilya.restcontrollerv100.service.Impl.UserServiceImpl;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserServiceImpl userServiceImpl;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userServiceImpl.findById(principal.getId())
                .filter(user -> !user.getStatus().equals(StatusEntity.DELETED))
                .switchIfEmpty(Mono.error(new UnauthorizedException("User disabled")))
                .map(user-> authentication);
    }
}