package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.FullCommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}")
@AllArgsConstructor
public class CommentsPrivController {

    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public FullCommentDto createComment(@PathVariable Integer userId, @PathVariable Long eventId, @Validated @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/comments/{commentId}")
    public FullCommentDto patchComment(@PathVariable Integer userId,
                                       @PathVariable Long commentId, @Validated @RequestBody UpdateCommentDto updateCommentDto) {
        return commentService.patchComment(userId, commentId, updateCommentDto);
    }

    @GetMapping
    public List<FullCommentDto> getCommentsByUser(@PathVariable Integer userId,
                                                  @Validated @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                  @Validated @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return commentService.getCommentsByUser(userId, from, size);
    }
}
