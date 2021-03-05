package com.atanava.tasks.service;

import com.atanava.tasks.model.Task;
import com.atanava.tasks.repository.CrudTaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.atanava.tasks.util.DateTimeUtil.atStartOfDayOrMin;
import static com.atanava.tasks.util.DateTimeUtil.atStartOfNextDayOrMax;
import static com.atanava.tasks.util.ValidationUtil.checkNotFoundWithId;

@Service
public class TaskService {

    private final CrudTaskRepository repository;

    public TaskService(CrudTaskRepository repository) {
        this.repository = repository;
    }

    public Task create(Task task) {
        Assert.notNull(task, "task must not be null");
        return repository.saveTask(task);
    }

    public void update(Task task) {
        Assert.notNull(task, "task must not be null");
        task.setModified(LocalDateTime.now());
        checkNotFoundWithId(repository.saveTask(task), task.id());
    }

    @Transactional
    public void complete(int id, boolean completed) {
        Task task = get(id);
        task.setCompleted(completed);
    }

    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id) != 0, id);
    }

    public Task get(int id) {
        return checkNotFoundWithId(repository.get(id), id);
    }

    public List<Task> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "created"));
    }

    public List<Task> getAllByCreatedRange(@Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        return repository.getAllByCreatedRange(atStartOfDayOrMin(startDate), atStartOfNextDayOrMax(endDate));
    }

    public List<Task> getAllByModifiedRange(@Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        return repository.getAllByModifiedRange(atStartOfDayOrMin(startDate), atStartOfNextDayOrMax(endDate));
    }

    public List<Task> getAllByCompleted(boolean completed) {
        return repository.getAllByCompleted(completed);
    }

    List<Task> getAllModified(boolean isModified) {
        return isModified ? repository.getAllModified() : repository.getAllUnmodified();
    }

}
