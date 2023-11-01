package ru.practicum.comments.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.comments.model.CommentStateAction;

import javax.validation.constraints.Size;
@Getter
@Setter
public class UpdateCommentDto extends NewCommentDto{
    CommentStateAction stateAction;

    public UpdateCommentDto(@Size(min = 10, max = 2000) String text, CommentStateAction stateAction) {
        super(text);
        this.stateAction = stateAction;
    }
}
