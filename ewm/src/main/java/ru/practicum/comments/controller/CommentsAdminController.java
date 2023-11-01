package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.FullCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/comments")
@AllArgsConstructor
public class CommentsAdminController {

    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<FullCommentDto> getCommentsForAdmin(@RequestParam(required = false) Long[] eventIds,
                                                    @RequestParam(required = false) String[] state,
                                                    @Validated @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                    @Validated @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return commentService.getCommentsForAdmin(eventIds, state, from, size);
    }

    @PatchMapping("/{commentId}")
    public FullCommentDto patchCommentForAdmin(
            @PathVariable Long commentId, @Validated @RequestBody UpdateCommentDto updateCommentDto) {
        return commentService.patchCommentForAdmin(commentId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
