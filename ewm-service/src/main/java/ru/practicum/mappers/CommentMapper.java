package ru.practicum.mappers;

import org.springframework.stereotype.Service;
import ru.practicum.adapters.DateTimeAdapter;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentFullDto;
import ru.practicum.dto.SubCommentDto;
import ru.practicum.models.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentMapper {

    public Comment dtoToObject(CommentCreateDto dto) {

        return Comment.builder()
                .text(dto.getText())
                .build();
    }

    public static CommentFullDto objectToFullDto(Comment comment) {

        List<SubCommentDto> subComments = new ArrayList<>();
        String updateOn = null;

        if (comment.getSubComments() != null && !comment.getSubComments().isEmpty()) {
            subComments = comment.getSubComments().stream().map(CommentMapper::objectToSubCommentDto).collect(Collectors.toList());
        }
        if (comment.getUpdateOn() != null) {
            updateOn = DateTimeAdapter.dateToString(comment.getUpdateOn());
        }

        return CommentFullDto.builder()
                .id(comment.getId())
                .createdOn(DateTimeAdapter.dateToString(comment.getCreatedOn()))
                .updateOn(updateOn)
                .commentator(UserMapper.objectToShortDto(comment.getCommentator()))
                .text(comment.getText())
                .subComments(subComments)
                .build();
    }

    public static SubCommentDto objectToSubCommentDto(Comment comment) {

        List<SubCommentDto> subComments = new ArrayList<>();
        String updateOn = null;

        if (comment.getSubComments() != null && !comment.getSubComments().isEmpty()) {

            subComments = comment.getSubComments().stream().map(CommentMapper::objectToSubCommentDto).collect(Collectors.toList());
        }
        if (comment.getUpdateOn() != null) {
            updateOn = DateTimeAdapter.dateToString(comment.getUpdateOn());
        }

        return SubCommentDto.builder()
                .id(comment.getId())
                .createdOn(DateTimeAdapter.dateToString(comment.getCreatedOn()))
                .updateOn(updateOn)
                .commentator(UserMapper.objectToShortDto(comment.getCommentator()))
                .text(comment.getText())
                .subComments(subComments)
                .build();
    }
}
