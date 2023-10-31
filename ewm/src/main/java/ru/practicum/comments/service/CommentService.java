package ru.practicum.comments.service;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.comments.dto.FullCommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    FullCommentDto createComment(Integer userId, Long eventId, NewCommentDto newCommentDto);
    public FullCommentDto patchComment(Integer userId, Long commentId, UpdateCommentDto updateCommentDto);
    List<FullCommentDto> getCommentsByUser (Integer userId);

}
