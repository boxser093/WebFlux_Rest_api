package net.ilya.restcontrollerv100.service;

import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.entity.EventEntity;

public interface EventService extends GenericService<EventEntity, Long, EventDto> {
}
