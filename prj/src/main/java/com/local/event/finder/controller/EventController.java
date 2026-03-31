package com.local.event.finder.controller;

import com.local.event.finder.logging.AppLogger;
import com.local.event.finder.logging.LoggerFactory;
import com.local.event.finder.model.dto.EventRequestDto;
import com.local.event.finder.model.entity.Event;
import com.local.event.finder.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody EventRequestDto eventDto){
        log.info("EventController:create");
        return eventService.create(eventDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Event getEventById(@PathVariable Long id){
        log.info("EventController:findById");
        return eventService.getById(id);
    }

    @GetMapping("/title")
    @ResponseStatus(HttpStatus.OK)
    public Event getEventByTitle(@RequestParam String title){
        log.info("EventController:getEventByTitle");
        return eventService.getByTitle(title);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Event> getAllEvents(){
        log.info("EventController:getAllEvents");
        return eventService.getAll();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Event updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDto eventDto){
        log.info("EventController:updateEvent");
        return eventService.update(id, eventDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        log.info("EventController:delete");
        eventService.delete(id);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> getEventsByUserId(@PathVariable Long userId){
        log.info("EventController:getEventsByUserId");
        return eventService.getEventByUser(userId);
    }
}
