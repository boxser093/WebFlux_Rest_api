package net.ilya.restcontrollerv100.mapper;

import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.springframework.security.core.userdetails.User;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto map(UserEntity userEntity);
    @InheritInverseConfiguration
    UserEntity map(UserDto userDto);


}
