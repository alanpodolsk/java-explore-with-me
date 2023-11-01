package ru.practicum.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.comments.model.CommentStateAction;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCommentDto {
    @Size(min = 10, max = 2000)
    String text;
    CommentStateAction stateAction;
}
