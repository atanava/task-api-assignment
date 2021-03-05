package com.atanava.tasks.web;

import com.atanava.tasks.model.Task;
import com.atanava.tasks.service.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.atanava.tasks.util.ValidationUtil.assureIdConsistent;
import static com.atanava.tasks.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = TaskRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskRestController {

    private static final Logger log = LoggerFactory.getLogger(TaskRestController.class);

    static final String REST_URL = "/rest/tasks";

    private final TaskService service;

    public TaskRestController(TaskService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Task> create(@Valid @RequestBody Task task) {
        checkNew(task);
        log.info("create new task");

        Task created = service.create(task);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Task task, @PathVariable int id) {
        assureIdConsistent(task, id);
        log.info("update task {}", id);
        service.update(task);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable int id, @RequestParam boolean completed) {
        log.info("set completed={} for task {}", completed, id);
        service.complete(id, completed);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete task {}", id);
        service.delete(id);
    }

    @GetMapping("/{id}")
    public Task get(@PathVariable int id) {
        log.info("get task {}", id);
        return service.get(id);
    }

    @GetMapping
    public List<Task> getAll() {
        log.info("get all tasks");
        return service.getAll();
    }

    @GetMapping("/filter")
    public List<Task> getAllFiltered(@RequestParam(required = false) Boolean completed,
                                     @RequestParam(required = false) String inRangeBy,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("get all tasks filtered by completed={} inRangeBy={} startDate={} endDate={}",
                completed, inRangeBy, startDate, endDate);

        return completed != null ? service.getAllByCompleted(completed)
                : (inRangeBy != null && inRangeBy.equals("created")) ? service.getAllByCreatedRange(startDate, endDate)
                : (inRangeBy != null && inRangeBy.equals("modified")) ? service.getAllByModifiedRange(startDate, endDate)
                : Collections.emptyList();
    }

}
