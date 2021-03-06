package com.atanava.tasks.repository;

import com.atanava.tasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudTaskRepository extends JpaRepository<Task, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.id=:id")
    int delete(@Param("id") int id);

    @Query("SELECT t FROM Task t WHERE t.added >= :startDate AND t.added < :endDate ORDER BY t.added DESC")
    List<Task> getAllByAddedRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Task t WHERE t.modified >= :startDate AND t.modified < :endDate ORDER BY t.added DESC")
    List<Task> getAllByModifiedRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Task t WHERE t.completed >= :startDate AND t.completed < :endDate ORDER BY t.completed DESC")
    List<Task> getAllByCompletedRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Task t WHERE t.completed IS NULL AND t.added >= :startDate AND t.added < :endDate ORDER BY t.added DESC")
    List<Task> getAllUncompletedByRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Task t WHERE t.completed IS NOT NULL ORDER BY t.added DESC")
    List<Task> getAllCompleted();

    @Query("SELECT t FROM Task t WHERE t.completed IS NULL ORDER BY t.added DESC")
    List<Task> getAllUnCompleted();

    @Query("SELECT t FROM Task t WHERE t.modified IS NOT NULL ORDER BY t.modified DESC")
    List<Task> getAllModified();

    @Query("SELECT t FROM Task t WHERE t.modified IS NULL ORDER BY t.added DESC")
    List<Task> getAllUnmodified();

    @Transactional
    default Task checkAndSave(Task task) {
        if (!task.isNew() && findById(task.getId()).isEmpty()) {
            return null;
        }
        return save(task);
    }

    default Task get(int id) {
        return findById(id).orElse(null);
    }
}
