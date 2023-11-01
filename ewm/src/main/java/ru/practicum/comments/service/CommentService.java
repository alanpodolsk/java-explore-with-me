package ru.practicum.comments.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.comments.dto.FullCommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    FullCommentDto createComment(Integer userId, Long eventId, NewCommentDto newCommentDto);

    FullCommentDto patchComment(Integer userId, Long commentId, UpdateCommentDto updateCommentDto);

    List<FullCommentDto> getCommentsByUser(Integer userId, Integer from, Integer size);

    List<FullCommentDto> getCommentsForAdmin(Long[] eventIds, String[] state, Integer from, Integer size);

    FullCommentDto patchCommentForAdmin(Long commentId, UpdateCommentDto updateCommentDto);

    void deleteComment(@PathVariable Long commentId);

}
