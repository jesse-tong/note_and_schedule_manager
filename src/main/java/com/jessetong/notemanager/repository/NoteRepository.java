package com.jessetong.notemanager.repository;
import com.jessetong.notemanager.entity.Note;
import com.jessetong.notemanager.entity.User;
import java.util.List;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByTitleContaining(String title);
    List<Note> findByTitleOrderByCreatedAt(String title);
    List<Note> findByTitleOrderByCreatedAtAsc(String title);
    List<Note> findByTitleIn(Collection<String> titles);
    List<Note> findByContentContaining(String content);
    List<Note> findByUserId(Long userId);
    List<Note> findByUser(User user);
    List<Note> findByUserOrderByCreatedAt(User user);
    List<Note> findByUserOrderByCreatedAtAsc(User user);
    List<Note> findByUserAndTitleContaining(User user, String title);
    List<Note> findByUserAndTitleContainingOrderByCreatedAt(User user, String title);
    List<Note> findByUserIdAndTitleContaining(Long userId, String title);
    List<Note> findByUserIdAndTitleOrderByCreatedAt(Long userId, String title);
    List<Note> findByUserIdAndTitleOrderByCreatedAtAsc(Long userId, String title);
    List<Note> findByUserIdAndTitleIn(Long userId, Collection<String> titles);
    List<Note> findByUserIdAndContentContaining(Long userId, String content);
    List<Note> findByUserIdAndContentContainingOrderByCreatedAt(Long userId, String content);
    Integer countByUserId(Long userId);
    Integer countByUserIdAndTitleContaining(Long userId, String title);
}
