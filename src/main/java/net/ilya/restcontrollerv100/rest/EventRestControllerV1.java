package net.ilya.restcontrollerv100.rest;

import lombok.RequiredArgsConstructor;
import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.mapper.EventMapper;
import net.ilya.restcontrollerv100.service.EventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class EventRestControllerV1 {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/api/v1/events/{id}")
    public Mono<EventDto> getEventById(@RequestParam @Validated Long id) {
        return eventService.findById(id).map(eventMapper::map);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/api/v1/events/")
    public Flux<EventDto> getEvents() {
        return eventService.findAll();
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PostMapping("/api/v1/events/")
    public Mono<EventDto> createNewEvent(@RequestBody EventDto eventDto) {
        return eventService.create(eventMapper.map(eventDto)).map(eventMapper::map);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PutMapping("/api/v1/events/")
    public Mono<EventDto> updateEvent(@RequestBody EventDto eventDto) {
        return eventService.update(eventMapper.map(eventDto)).map(eventMapper::map);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @DeleteMapping("/api/v1/events/")
    public Mono<EventDto> deletedEvent(@RequestParam @Validated Long id) {
        return eventService.delete(id).map(eventMapper::map);
    }
}
