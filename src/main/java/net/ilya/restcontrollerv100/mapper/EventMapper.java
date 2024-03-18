package net.ilya.restcontrollerv100.mapper;

import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.entity.EventEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FileMapper.class})
public interface EventMapper {


    @Mapping(source = "eventEntity.userEntity", target = "userDto")
    @Mapping(source = "eventEntity.fileEntity", target = "fileDto")
    EventDto map(EventEntity eventEntity);

    @InheritInverseConfiguration
    EventEntity map(EventDto eventDto);


}
