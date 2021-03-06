package com.atanava.tasks.service;

import com.atanava.tasks.AbstractTest;
import com.atanava.tasks.model.Task;
import com.atanava.tasks.util.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolationException;
import java.sql.BatchUpdateException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.atanava.tasks.TaskTestData.*;

class TaskServiceTest extends AbstractTest {

    @Autowired
    TaskService service;

    @Test
    void create() {
        Task created = service.create(getNew());
        int newId = created.id();
        Task newTask = getNew();
        newTask.setId(newId);

        IGNORE_DATETIME_TASK_MATCHER.assertMatch(created, newTask);
        IGNORE_DATETIME_TASK_MATCHER.assertMatch(service.get(newId), newTask);
    }

    @Test
    void createWithException() {
        validateRootCause(() -> service.create(new Task(null, "", now, null, null)),
                ConstraintViolationException.class);
        validateRootCause(() -> service.create(new Task(null, null, now, null, null)),
                ConstraintViolationException.class);
        validateRootCause(() -> service.create(new Task(null, "OK", now, now, null)),
                BatchUpdateException.class);
        validateRootCause(() -> service.create(new Task(null, "OK", now, now.minusSeconds(1), null)),
                BatchUpdateException.class);
        validateRootCause(() -> service.create(new Task(null, "OK", now, null, now.minusSeconds(1))),
                BatchUpdateException.class);
        validateRootCause(() -> service.create(new Task(null, "OK", now, now.plusMinutes(10), now.plusMinutes(5))),
                BatchUpdateException.class);
    }

    @Test
    void update() {
        Task updated = getUpdated();
        service.update(updated);
        updated = service.get(TASK_5);
        assertNotNull(updated.getModified());

        IGNORE_DATETIME_TASK_MATCHER.assertMatch(updated, getUpdated());
    }

    @Test
    void complete() {
        service.complete(TASK_4, true);
        assertTrue(service.get(TASK_4).isCompleted());
    }

    @Test
    void updateNotFound() {
        assertThrows(NotFoundException.class, () -> service.update(getNotExist()));
    }

    @Test
    void delete() {
        service.delete(TASK_2);
        assertThrows(NotFoundException.class, () -> service.get(TASK_2));
    }

    @Test
    void get() {
        Task actual = service.get(TASK_2);
        TASK_MATCHER.assertMatch(actual, task2);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_EXIST));
    }

    @Test
    void getAll() {
        List<Task> allActual = service.getAll();
        TASK_MATCHER.assertMatch(allActual, getAllExpected());
    }

    @Test
    void getAllByCreatedRange() {
        List<Task> allActual = service.getAllByFilter("added", day1, day1);
        TASK_MATCHER.assertMatch(allActual, task2, task1);

        allActual = service.getAllByFilter("added", day2, null);
        TASK_MATCHER.assertMatch(allActual, task5, task4, task3);
    }

    @Test
    void getAllByModifiedRange() {
        List<Task> allActual = service.getAllByFilter("modified", day1, day1);
        TASK_MATCHER.assertMatch(allActual, task2);

        allActual = service.getAllByFilter("modified", day2, day2);
        TASK_MATCHER.assertMatch(allActual, task4);
    }

    @Test
    void getAllByCompletedRange() {
        List<Task> allActual = service.getAllByFilter("completed", day2, day2);
        TASK_MATCHER.assertMatch(allActual, task1);

        allActual = service.getAllByFilter("completed", day2, day3);
        TASK_MATCHER.assertMatch(allActual, task3, task1);
    }

    @Test
    void getAllUncompletedByRange() {
        List<Task> allActual = service.getAllByFilter("uncompleted", day1, day1);
        TASK_MATCHER.assertMatch(allActual, task2);

        allActual = service.getAllByFilter("uncompleted", day2, day3);
        TASK_MATCHER.assertMatch(allActual, task5, task4);
    }

    @Test
    void getAllByCompleted() {
        List<Task> allActual = service.getAllByCompleted(true);
        TASK_MATCHER.assertMatch(allActual, task3, task1);

        allActual = service.getAllByCompleted(false);
        TASK_MATCHER.assertMatch(allActual, task5, task4, task2);
    }

    @Test
    void getAllModified() {
        List<Task> allActual = service.getAllModified(true);
        TASK_MATCHER.assertMatch(allActual, task4, task2);

        allActual = service.getAllModified(false);
        TASK_MATCHER.assertMatch(allActual, task5, task3, task1);
    }

}