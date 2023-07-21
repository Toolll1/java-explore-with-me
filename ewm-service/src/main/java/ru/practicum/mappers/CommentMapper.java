package ru.practicum.mappers;

import org.springframework.stereotype.Service;
import ru.practicum.adapters.DateTimeAdapter;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentFullDto;
import ru.practicum.models.Comment;
import ru.practicum.repositories.CommentRepository;

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

    public static CommentFullDto objectToFullDto(Comment comment, CommentRepository commentRepository) {

        List<CommentFullDto> subComments = new ArrayList<>();
        String updateOn = null;

        if (comment.getSubComments() != null && !comment.getSubComments().isEmpty()) {
            subComments = commentRepository.findAllByParentId(comment.getId())
                    .stream()
                    .map(x -> CommentMapper.objectToFullDto(x, commentRepository))
                    .collect(Collectors.toList());
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
}
