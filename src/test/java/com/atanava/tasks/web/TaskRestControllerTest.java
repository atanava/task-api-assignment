package com.atanava.tasks.web;

import com.atanava.tasks.model.Task;
import com.atanava.tasks.service.TaskService;
import com.atanava.tasks.util.exception.NotFoundException;
import com.atanava.tasks.web.json.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.atanava.tasks.TaskTestData.*;
import static com.atanava.tasks.TestUtil.*;
import static com.atanava.tasks.util.exception.ErrorType.VALIDATION_ERROR;
import static com.atanava.tasks.util.exception.ErrorType.DATA_NOT_FOUND;

@ActiveProfiles("postgres")
class TaskRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = TaskRestController.REST_URL + '/';

    @Autowired
    TaskService service;

    @Test
    void create() throws Exception {
        Task newTask = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTask)));

        Task created = readFromJson(action, Task.class);
        int newId = created.id();
        newTask.setId(newId);
        IGNORE_DATETIME_TASK_MATCHER.assertMatch(created, newTask);
        IGNORE_DATETIME_TASK_MATCHER.assertMatch(service.get(newId), newTask);
    }

    @Test
    void createInvalid() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(new Task())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void update() throws Exception {
        Task updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + TASK_5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        IGNORE_DATETIME_TASK_MATCHER.assertMatch(service.get(TASK_5), updated);
    }

    @Test
    void updateInvalid() throws Exception {
        Task invalid = getUpdated();
        invalid.setDescription("");
        perform(MockMvcRequestBuilders.put(REST_URL + TASK_5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateWithWrongId() throws Exception {
        Task updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + TASK_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateNotFound() throws Exception {
        Task updated = getUpdated();
        updated.setId(NOT_EXIST);
        perform(MockMvcRequestBuilders.put(REST_URL + NOT_EXIST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void complete() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + TASK_2)
                .param("completed", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertTrue(service.get(TASK_2).isCompleted());
    }

    @Test
    void completeNotFound() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + NOT_EXIST)
                .param("completed", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + TASK_1))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(TASK_1));
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_EXIST))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + TASK_1))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TASK_MATCHER.contentJson(task1));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TASK_MATCHER.contentJson(getAllExpected()));
    }

    @Test
    void getAllByCreatedRange() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("inRangeBy", "added")
                .param("startDate", String.valueOf(day2)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TASK_MATCHER.contentJson(task5, task4, task3));
    }

    @Test
    void getAllByModifiedRange() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("inRangeBy", "modified")
                .param("startDate", String.valueOf(day1))
                .param("endDate", String.valueOf(day1)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TASK_MATCHER.contentJson(List.of(task2)));
    }

    @Test
    void getAllByCompletedRange() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("inRangeBy", "completed")
                .param("startDate", String.valueOf(day2))
                .param("endDate", String.valueOf(day3)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TASK_MATCHER.contentJson(task3, task1));
    }

    @Test
    void getAllUncompletedByRange() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("inRangeBy", "uncompleted")
                .param("startDate", String.valueOf(day2))
                .param("endDate", String.valueOf(day3)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TASK_MATCHER.contentJson(task5, task4));
    }

    @Test
    void getAllCompleted() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("completed", "true"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TASK_MATCHER.contentJson(task3, task1));
    }

    @Test
    void getAllUncompleted() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("completed", "false"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TASK_MATCHER.contentJson(task5, task4, task2));
    }

    @Test
    void getAllModified() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("modified", "true"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TASK_MATCHER.contentJson(task4, task2));
    }

    @Test
    void getAllUnmodified() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter")
                .param("modified", "false"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TASK_MATCHER.contentJson(task5, task3, task1));
    }
}