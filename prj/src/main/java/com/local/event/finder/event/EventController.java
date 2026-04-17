package com.local.event.finder.event;

import com.local.event.finder.api.ApiResponseDto;
import com.local.event.finder.api.StatusResponseDto;
import com.local.event.finder.logging.AppLogger;
import com.local.event.finder.logging.LoggerFactory;
import com.local.event.finder.event.participant.EventParticipantResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final static AppLogger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<StatusResponseDto>> create(@Valid @RequestBody EventRequestDto eventDto){
        log.info("EventController:create");
        eventService.create(eventDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(new StatusResponseDto("OK")));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<EventResponseDto> getEventById(@PathVariable Long id){
        log.info("EventController:findById");
        return new ApiResponseDto<>(eventService.getEventResponseById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<List<EventResponseDto>> getAllEvents(){
        log.info("EventController:getAllEvents");
        return new ApiResponseDto<>(eventService.getAll());
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<EventResponseDto> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDto eventDto){
        log.info("EventController:updateEvent");
        return new ApiResponseDto<>(eventService.update(id, eventDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<StatusResponseDto> delete(@PathVariable Long id){
        log.info("EventController:delete");
        eventService.delete(id);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

    @PostMapping("/{id}/participants")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<StatusResponseDto> joinEvent(@PathVariable Long id){
        log.info("EventController:joinEvent");
        eventService.joinEvent(id);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

    @DeleteMapping("/{id}/participants/me")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<StatusResponseDto> leaveEvent(@PathVariable Long id){
        log.info("EventController:leaveEvent");
        eventService.leaveEvent(id);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

    @GetMapping("/{id}/participants")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<List<EventParticipantResponseDto>> getParticipants(@PathVariable Long id) {
        log.info("EventController:getParticipants");
        return new ApiResponseDto<>(eventService.getParticipants(id));
    }

    @PostMapping("/{id}/categories/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<StatusResponseDto> assignCategory(@PathVariable Long id, @PathVariable Long categoryId){
        log.info("EventController:assignCategory");
        eventService.assignCategory(id, categoryId);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }

    @DeleteMapping("/{id}/categories/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDto<StatusResponseDto> removeCategory(@PathVariable Long id, @PathVariable Long categoryId){
        log.info("EventController:removeCategory");
        eventService.removeCategory(id, categoryId);
        return new ApiResponseDto<>(new StatusResponseDto("OK"));
    }
}
