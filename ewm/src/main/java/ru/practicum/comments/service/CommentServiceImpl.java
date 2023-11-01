package ru.practicum.comments.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentMapper;
import ru.practicum.comments.dto.FullCommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.CommentState;
import ru.practicum.comments.model.CommentStateAction;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventsState;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UsersRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final UsersRepository usersRepository;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public FullCommentDto createComment(Integer userId, Long eventId, NewCommentDto newCommentDto) {
        Optional<User> userOpt = usersRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        Optional<Event> eventOpt = eventRepository.getByIdAndState(eventId, EventsState.PUBLISHED);
        if (eventOpt.isEmpty()) {
            throw new NoObjectException(String.format("Published event with id = %s was not found", eventId));
        }
        Comment comment = new Comment(
                null,
                eventOpt.get(),
                userOpt.get(),
                newCommentDto.getText(),
                CommentState.PENDING
        );
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public FullCommentDto patchComment(Integer userId, Long commentId, UpdateCommentDto updateCommentDto) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            throw new NoObjectException(String.format("Comment with id = %s was not found", commentId));
        }
        Comment comment = commentOpt.get();
        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new ConflictException("Incorrect user try to patch comment");
        }
        if (updateCommentDto.getStateAction() == CommentStateAction.CANCEL) {
            comment.setState(CommentState.CANCELED);
        } else if (updateCommentDto.getText() != null && !updateCommentDto.getText().equals(comment.getText())) {
            comment.setText(updateCommentDto.getText());
            comment.setState(CommentState.PENDING);
        } else {
            throw new ValidationException("This command is not operated");
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<FullCommentDto> getCommentsByUser(Integer userId, Integer from, Integer size) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        return CommentMapper.toFullCommentDtoList(commentRepository.findByUserId(userId, PageRequest.of(from / size, size)).getContent());
    }

    @Override
    public List<FullCommentDto> getCommentsForAdmin(Long[] eventIds, CommentState[] state, Integer from, Integer size) {
        List<Comment> comments;
        boolean eventExists = false;
        boolean stateExists = false;
        if (eventIds != null && eventIds.length > 0) {
            eventExists = true;
        }
        if (state != null && state.length > 0) {
            stateExists = true;
        }
        if (eventExists && stateExists) {
            comments = commentRepository.findByEventIdInAndStateIn(Arrays.asList(eventIds), Arrays.asList(state), PageRequest.of(from / size, size)).getContent();
        } else if (eventExists) {
            comments = commentRepository.findByEventIdIn(Arrays.asList(eventIds), PageRequest.of(from / size, size)).getContent();

        } else if (stateExists) {
            comments = commentRepository.findByStateIn(Arrays.asList(state), PageRequest.of(from / size, size)).getContent();
        } else {
            comments = commentRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }
        return CommentMapper.toFullCommentDtoList(comments);
    }

    @Override
    @Transactional
    public FullCommentDto patchCommentForAdmin(Long commentId, UpdateCommentDto updateCommentDto) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            throw new NoObjectException(String.format("Comment with id = %s was not found", commentId));
        }
        Comment comment = commentOpt.get();
        if (comment.getState().equals(CommentState.PENDING)) {
            if (updateCommentDto.getStateAction().equals(CommentStateAction.PUBLISH)) {
                comment.setState(CommentState.PUBLISHED);
            } else if (updateCommentDto.getStateAction().equals(CommentStateAction.REJECT)) {
                comment.setState(CommentState.REJECTED);
            } else {
                throw new ConflictException("This command is not operated");
            }
        } else {
            throw new ConflictException("Admin can patch only pending comments");
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NoObjectException(String.format("Comment with id = %s was not found", commentId));
        }
        commentRepository.deleteById(commentId);
    }
}
