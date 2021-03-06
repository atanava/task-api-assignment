package com.atanava.tasks.web.json;


import com.atanava.tasks.model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.atanava.tasks.TaskTestData.*;

class JsonUtilTest {

    @Test
    public void readWriteValue() {
        Task task = task1;
        String json = JsonUtil.writeValue(task);
        System.out.println(json);
        Task taskFromJson = JsonUtil.readValue(json, Task.class);
        TASK_MATCHER.assertMatch(taskFromJson, task);
    }

    @Test
    public void readWriteValues() {
        List<Task> tasks = getAllExpected();
        String json = JsonUtil.writeValue(tasks);
        System.out.println(json);
        List<Task> tasksFromJson = JsonUtil.readValues(json, Task.class);
        TASK_MATCHER.assertMatch(tasksFromJson, tasks);
    }


}