package net.ilya.restcontrollerv100.security;

import io.jsonwebtoken.Claims;
import net.ilya.restcontrollerv100.entity.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import javax.management.relation.Role;
import java.util.List;

public class UserAuthBarrier {
    public static Mono<Authentication> create(JwtHandler.VerificationResult verificationResult){
        Claims claims = verificationResult.claims;
        String subject = claims.getSubject();

        String role = claims.get("role", String.class);
        String username = claims.get("username", String.class);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        Long principalId = Long.parseLong(subject);
        CustomPrincipal principal = new CustomPrincipal(principalId, username);

        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(principal,null,authorities));
    }
}
