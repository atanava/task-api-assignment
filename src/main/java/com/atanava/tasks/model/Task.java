package com.atanava.tasks.model;

import com.atanava.tasks.util.DateTimeUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"modified", "added"}, name = "chk_modified"),
        @UniqueConstraint(columnNames = {"completed", "added", "modified"}, name = "chk_completed")
})
public class Task extends AbstractBaseEntity {

    @Column(name = "description", nullable = false)
    @NotBlank
    private String description;

    @Column(name = "added", nullable = false)
    @NotNull
    @DateTimeFormat(pattern = DateTimeUtil.DATE_TIME_PATTERN)
    private LocalDateTime added;

    @Column(name = "modified", nullable = false)
    @DateTimeFormat(pattern = DateTimeUtil.DATE_TIME_PATTERN)
    private LocalDateTime modified;

    @Column(name = "completed")
    @DateTimeFormat(pattern = DateTimeUtil.DATE_TIME_PATTERN)
    private LocalDateTime completed;

    public Task() {}

    public Task(String description) {
        this(null, description, LocalDateTime.now(),null, null);
    }

    public Task(Integer id, String description, LocalDateTime added, LocalDateTime modified, LocalDateTime completed) {
        super(id);
        this.description = description;
        this.added = added;
        this.modified = modified;
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getAdded() {
        return added;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public boolean isCompleted() {
        return completed != null;
    }

    public LocalDateTime getCompleted() {
        return completed;
    }

    public void setCompleted(LocalDateTime completed) {
        this.completed = completed;
    }
}
