package com.jessetong.notemanager.repository;
import com.jessetong.notemanager.entity.Schedule;
import java.util.*;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    public List<Schedule> findByTitleContaining(String title);
    public List<Schedule> findByTitleOrderByCreatedAt(String title);
    public List<Schedule> findByTitleOrderByCreatedAtAsc(String title);
    public List<Schedule> findByTitleIn(Collection<String> titles);
    public List<Schedule> findByDescriptionContaining(String description);
    public List<Schedule> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    public List<Schedule> findByEndTimeBetween(LocalDateTime start, LocalDateTime end);
    public List<Schedule> findByStartTimeGreaterThanEqual(LocalDateTime start);
    public List<Schedule> findByEndTimeLessThanEqual(LocalDateTime end);
    public List<Schedule> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(LocalDateTime start, LocalDateTime end);
    public Page<Schedule> findByUserId(Long userId, Pageable pageable);
    public List<Schedule> findByUserIdAndTitleContaining(Long userId, String title);
    public List<Schedule> findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(Long userId, LocalDateTime start, LocalDateTime end);
    public List<Schedule> findByUserIdAndStartTimeGreaterThanEqual(Long userId, LocalDateTime start);
    public List<Schedule> findByUserIdAndEndTimeLessThanEqual(Long userId, LocalDateTime end);
    public Integer countByUserId(Long userId);
    public Integer countByUserIdAndTitleContaining(Long userId, String title);
    public Integer countByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    public Integer countByEndTimeBetween(LocalDateTime start, LocalDateTime end);
}