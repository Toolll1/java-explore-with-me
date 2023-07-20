package ru.practicum.mappers;

import org.springframework.stereotype.Service;
import ru.practicum.adapters.DateTimeAdapter;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentFullDto;
import ru.practicum.dto.CommentFullUpdateDto;
import ru.practicum.models.Comment;

@Service
public class CommentMapper {

    public static CommentCreateDto objectToDto(Comment comment) {

        return CommentCreateDto.builder()
                .text(comment.getText())
                .build();
    }

    public Comment dtoToObject(CommentCreateDto dto) {

        return Comment.builder()
                .text(dto.getText())
                .build();
    }

    public static CommentFullDto objectToFullDto(Comment comment) {

        return CommentFullDto.builder()
                .id(comment.getId())
                .createdOn(DateTimeAdapter.dateToString(comment.getCreatedOn()))
                .commentator(UserMapper.objectToShortDto(comment.getCommentator()))
                .text(comment.getText())
                .build();
    }

    public static CommentFullUpdateDto objectToFullUpdateDto(Comment comment) {

        return CommentFullUpdateDto.builder()
                .id(comment.getId())
                .createdOn(DateTimeAdapter.dateToString(comment.getCreatedOn()))
                .updateOn(DateTimeAdapter.dateToString(comment.getUpdateOn()))
                .commentator(UserMapper.objectToShortDto(comment.getCommentator()))
                .text(comment.getText())
                .build();
    }
}
