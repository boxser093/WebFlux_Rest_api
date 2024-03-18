package net.ilya.restcontrollerv100.mapper;


import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {EventMapper.class})
public interface UserMapper {

    @Mapping(source = "userEntity.eventEntityList", target = "eventDto")
    UserDto map(UserEntity userEntity);
    @InheritInverseConfiguration
    UserEntity map(UserDto userDto);

}
