package controllers.priv;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.EwmServer;
import ru.practicum.controllers.priv.CommentControllerPrivate;
import ru.practicum.controllers.priv.EventControllerPrivate;
import ru.practicum.controllers.pub.CommentControllerPublic;
import ru.practicum.dto.*;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.models.Ban;
import ru.practicum.models.User;
import ru.practicum.repositories.BanRepository;
import ru.practicum.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EwmServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentControllerPrivateTests {

    private final EventControllerPrivate eventControllerPrivate;
    private final CommentControllerPrivate commentControllerPrivate;
    private final CommentControllerPublic commentControllerPublic;
    private final UserRepository userRepository;
    private final BanRepository banRepository;

    User user;
    EventFullDto event;
    CommentFullDto comment;

    @BeforeEach
    void beforeEach() {

        user = userRepository.findById(1L).get();
        event = eventControllerPrivate.getEventPrivate(user.getId(), 1L, 0, 10);
    }

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectCommentFullDto_underNormalConditions() {

        //when
        createComment();

        //then
        assertEquals(comment.getId(), 1);
        assertEquals(comment.getCommentator().getId(), user.getId());
        assertEquals(comment.getText(), "не понравилось");
    }

    @DirtiesContext
    @Test
    public void create_returnsException_ifThereIsABan() {

        //given
        LocalDateTime now = LocalDateTime.now();
        Ban ban = Ban.builder().id(1L).createdOn(now.minusDays(2)).endOfBan(now.plusDays(1)).commentator(user).build();
        banRepository.save(ban);

        //then
        assertThrows(BadRequestException.class, this::createComment);
    }

    @DirtiesContext
    @Test
    public void create_returnException_ifTheCommentTextIsIncorrect() {

        //then
        assertThrows(DataIntegrityViolationException.class, this::createWithEmptyText);
        assertThrows(DataIntegrityViolationException.class, this::createWithTextIsNull);
        assertThrows(ObjectNotFoundException.class, this::createWithIncorrectUserId);
        assertThrows(ObjectNotFoundException.class, this::createWithIncorrectEventId);
    }

    @DirtiesContext
    @Test
    public void update_returnsTheCorrectCommentFullDto_underNormalConditions() {

        //given
        createComment();

        //when
        CommentFullUpdateDto newComment = commentControllerPrivate.updateComment(CommentCreateDto.builder()
                        .text("жена сказала, что я не прав и мероприятие очень хорошее..").build(),
                comment.getId(), user.getId(), event.getId());

        //then
        assertEquals(newComment.getId(), 1);
        assertEquals(newComment.getCommentator().getId(), user.getId());
        assertEquals(newComment.getText(), "жена сказала, что я не прав и мероприятие очень хорошее..");
        assertNotNull(newComment.getCreatedOn());
        assertNotNull(newComment.getUpdateOn());
    }

    @DirtiesContext
    @Test
    public void update_returnsException_ifTheCommentTextIsIncorrect() {

        //when
        createComment();

        //then
        assertThrows(DataIntegrityViolationException.class, this::updateWithEmptyText);
        assertThrows(DataIntegrityViolationException.class, this::updateWithTextIsNull);
        assertThrows(ObjectNotFoundException.class, this::updateWithIncorrectUserId);
        assertThrows(ObjectNotFoundException.class, this::updateWithIncorrectEventId);
        assertThrows(ObjectNotFoundException.class, this::updateWithIncorrectCommentId);
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        //given
        createComment();
        Long commentId = comment.getId();

        //when
        commentControllerPrivate.deleteComment(comment.getId(), user.getId(), event.getId());

        //then
        assertThrows(ObjectNotFoundException.class, () -> commentControllerPublic.getComment(commentId));
        assertEquals(commentControllerPublic.getComments(1L, 0, 10), new ArrayList<>());
    }

    private void updateWithEmptyText() {

        commentControllerPrivate.updateComment(
                CommentCreateDto.builder().text("").build(), comment.getId(), user.getId(), event.getId());
    }

    private void updateWithTextIsNull() {

        commentControllerPrivate.updateComment(
                CommentCreateDto.builder().build(), comment.getId(), user.getId(), event.getId());
    }

    private void updateWithIncorrectCommentId() {

        commentControllerPrivate.updateComment(CommentCreateDto.builder()
                        .text("жена сказала, что я не прав и мероприятие очень хорошее..").build(),
                999L, user.getId(), event.getId());
    }

    private void updateWithIncorrectEventId() {

        commentControllerPrivate.updateComment(CommentCreateDto.builder()
                        .text("жена сказала, что я не прав и мероприятие очень хорошее..").build(),
                comment.getId(), user.getId(), 999L);
    }

    private void updateWithIncorrectUserId() {

        commentControllerPrivate.updateComment(CommentCreateDto.builder()
                        .text("жена сказала, что я не прав и мероприятие очень хорошее..").build(),
                comment.getId(), 999L, event.getId());
    }

    private void createWithEmptyText() {

        comment = commentControllerPrivate.createComment(CommentCreateDto.builder().text("").build(), user.getId(), event.getId());
    }

    private void createWithTextIsNull() {

        comment = commentControllerPrivate.createComment(CommentCreateDto.builder().build(), user.getId(), event.getId());
    }

    private void createWithIncorrectUserId() {

        comment = commentControllerPrivate.createComment(
                CommentCreateDto.builder().text("не понравилось").build(), 999L, event.getId());
    }

    private void createWithIncorrectEventId() {

        comment = commentControllerPrivate.createComment(
                CommentCreateDto.builder().text("не понравилось").build(), user.getId(), 999L);
    }

    private void createComment() {

        comment = commentControllerPrivate.createComment(
                CommentCreateDto.builder().text("не понравилось").build(), user.getId(), event.getId());
    }
}
