package net.ilya.restcontrollerv100.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("users")
public class UserEntity {
    @Id
    private Long id;
    private String username;
    private String password;
    private UserRole role;
    private String firstName;
    private String lastName;
    private StatusEntity status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Flux<EventEntity> eventEntityList;
    @ToString.Include(name = "password")
    private String maskPassword(){
        return "********";
    }
}
