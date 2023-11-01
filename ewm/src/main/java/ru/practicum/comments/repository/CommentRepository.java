package ru.practicum.comments.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByUserId(Integer userId, Pageable pageable);

    Page<Comment> findByEventIdInAndStateIn(List<Long> eventIds, List<String> states, Pageable pageable);

    List<Comment> findByEventIdInAndState(List<Long> eventIds, String state);

    Page<Comment> findByEventIdIn(List<Long> eventIds, Pageable pageable);

    Page<Comment> findByStateIn(List<String> states, Pageable pageable);

    @NotNull Page<Comment> findAll(@NotNull Pageable pageable);
}
