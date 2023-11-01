package ru.practicum.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.comments.model.CommentState;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FullCommentDto {
    Long id;
    Long eventId;
    Integer userId;
    String text;
    CommentState commentState;

}
