package ru.practicum.comments.dto;

import ru.practicum.comments.model.Comment;
import ru.practicum.users.dto.UserMapper;

public class CommentMapper {

    public static FullCommentDto toCommentDto (Comment comment){
        return new FullCommentDto(
                comment.getId(),
                comment.getEvent().getId(),
                comment.getUser().getId(),
                comment.getText()
        );
    }

    public static ShortCommentDto toShortCommentDto(Comment comment){
        return new ShortCommentDto(
                UserMapper.toUserShortDto(comment.getUser()),
                comment.getText()
        );
    }
}
