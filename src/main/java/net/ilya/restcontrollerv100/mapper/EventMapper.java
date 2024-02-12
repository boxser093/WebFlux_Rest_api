package net.ilya.restcontrollerv100.mapper;

import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.entity.EventEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import reactor.core.publisher.Flux;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDto map(EventEntity eventEntity);

    @InheritInverseConfiguration
    EventEntity map(EventDto eventDto);

}
