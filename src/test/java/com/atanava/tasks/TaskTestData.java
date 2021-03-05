package com.atanava.tasks;

import com.atanava.tasks.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.atanava.tasks.model.Task.START_SEQ;
import static java.time.LocalDateTime.parse;

public class TaskTestData {

    public static TestMatcher<Task> TASK_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Task.class);
    public static TestMatcher<Task> IGNORE_DATETIME_TASK_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Task.class,
            "created", "modified");

    public static final int TASK_1 = START_SEQ;
    public static final int TASK_2 = START_SEQ + 1;
    public static final int TASK_3 = START_SEQ + 2;
    public static final int TASK_4 = START_SEQ + 3;
    public static final int TASK_5 = START_SEQ + 4;
    public static final int NEW_TASK = START_SEQ + 5;
    public static final int NOT_EXIST = 1000;

    public static LocalDateTime now = LocalDateTime.now();
    public static LocalDate day1 = LocalDate.parse("2021-03-03");
    public static LocalDate day2 = LocalDate.parse("2021-03-04");

    public static Task task1 = new Task(TASK_1,"Do work 1", parse("2021-03-03T10:00:00"), null, true);
    public static Task task2 = new Task(TASK_2,"Do work 2", parse("2021-03-03T12:00:00"), parse("2021-03-03T23:59:59"), false);
    public static Task task3 = new Task(TASK_3,"Do work 3", parse("2021-03-04T09:00:00"), null, true);
    public static Task task4 = new Task(TASK_4,"Do work 4", parse("2021-03-04T10:00:00"), parse("2021-03-04T12:00:00"), false);
    public static Task task5 = new Task(TASK_5,"Do work 5", parse("2021-03-04T14:25:10"), null, false);

    public static Task getNew() {
        return  new Task("new task");
    }

    public static Task getUpdated() {
        return new Task(TASK_5, "Modified task", task5.getCreated(), null, false);
    }

    public static Task getNotExist() {
        return new Task(NOT_EXIST, "OK", now, null, false);
    }

    public static List<Task> getAllExpected() {
        return List.of(task5, task4, task3, task2, task1);
    }

    public static List<Task> getCreatedAtFirstDay() {
        return List.of(task2, task1);
    }

    public static List<Task> getCreatedAtSecondDay() {
        return List.of(task5, task4, task3);
    }

    public static List<Task> getModifiedAtFirstDay() {
        return List.of(task2, task1);
    }
}
