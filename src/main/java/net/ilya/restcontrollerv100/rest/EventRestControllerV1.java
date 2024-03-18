package net.ilya.restcontrollerv100.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.restcontrollerv100.dto.EventDto;
import net.ilya.restcontrollerv100.dto.UserDto;
import net.ilya.restcontrollerv100.mapper.EventMapper;
import net.ilya.restcontrollerv100.service.EventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Event controller", description = "Операция с евентами")
@RequestMapping("/api/v1/events")
public class EventRestControllerV1 {
    private final EventService eventService;
    private final EventMapper eventMapper;
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Получиние евента по идентификатору",
            description = "Вы можете получить запись события по идентификатору")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping(value = "/{id}")
    public Mono<EventDto> getEventById(@PathVariable @Validated @Parameter(description = "Идентификатор пользователя",
            in = ParameterIn.PATH, name = "id", required = true, schema = @Schema(
            defaultValue = "1",
            minimum = "1",
            allOf = {Integer.class} ),
            style = ParameterStyle.SIMPLE) Long id) {
        return eventService.findById(id).map(eventMapper::map);
    }
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Получение списка евентов",
            description = "Вы можете получить список всех евентов")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/")
    public Flux<EventDto> getEvents() {
        return eventService.findAll();
    }
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Создание нового евента",
            description = "Вы можете создать новый евент без привязки к загрузке файла")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PostMapping(value = "/", produces = "application/json")
    public Mono<EventDto> createNewEvent(@RequestBody
                                             @Parameter(description = "Информация о евенте",
                                         schema = @Schema(additionalPropertiesSchema =EventDto.class)) EventDto eventDto) {
        return eventService.create(eventMapper.map(eventDto)).map(eventMapper::map);
    }
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Обновить евент",
            description = "Вы можете обновить существующую запись о событии")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @PutMapping(value = "/", produces = "application/json")
    public Mono<EventDto> updateEvent(@RequestBody @Parameter(description = "Информация о евенте",
            schema = @Schema(additionalPropertiesSchema =EventDto.class)) EventDto eventDto) {
        return eventService.update(eventMapper.map(eventDto)).map(eventMapper::map);
    }
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Удалить запись о евенте",
            description = "Вы можете совершить мягкое удаление евента")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @DeleteMapping(value = "/{id}")
    public Mono<EventDto> deletedEvent(@PathVariable @Validated @Parameter(description = "Идентификатор пользователя",
            in = ParameterIn.PATH, name = "id", required = true, schema = @Schema(
            defaultValue = "1",
            minimum = "1",
            allOf = {Integer.class} ),
            style = ParameterStyle.SIMPLE) Long id) {
        return eventService.delete(id).map(eventMapper::map);
    }
}
