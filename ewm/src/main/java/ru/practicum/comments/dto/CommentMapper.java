package ru.practicum.comments.dto;

import ru.practicum.comments.model.Comment;
import ru.practicum.users.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static FullCommentDto toCommentDto(Comment comment) {
        return new FullCommentDto(
                comment.getId(),
                comment.getEvent().getId(),
                comment.getUser().getId(),
                comment.getText(),
                comment.getState()
        );
    }

    public static ShortCommentDto toShortCommentDto(Comment comment) {
        return new ShortCommentDto(
                UserMapper.toUserShortDto(comment.getUser()),
                comment.getText()
        );
    }

    public static List<FullCommentDto> toFullCommentDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    public static List<ShortCommentDto> toShortCommentDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toShortCommentDto).collect(Collectors.toList());
    }
}
