package controllers.pub;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.EwmServer;
import ru.practicum.controllers.priv.CommentControllerPrivate;
import ru.practicum.controllers.priv.EventControllerPrivate;
import ru.practicum.controllers.pub.CommentControllerPublic;
import ru.practicum.dto.*;
import ru.practicum.models.User;
import ru.practicum.repositories.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EwmServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentControllerPublicTests {

    private final EventControllerPrivate eventControllerPrivate;
    private final CommentControllerPrivate commentControllerPrivate;
    private final CommentControllerPublic commentControllerPublic;
    private final UserRepository userRepository;

    User user;
    EventFullDto event;
    CommentFullDto comment;

    @BeforeEach
    void beforeEach() {

        user = userRepository.findById(1L).get();
        event = eventControllerPrivate.getEventPrivate(user.getId(), 1L, 0, 10);
        comment = commentControllerPrivate.createComment(CommentCreateDto.builder().text("не понравилось").build(), user.getId(), event.getId());
    }

    @DirtiesContext
    @Test
    public void getComment_returnsCommentFullDto_underNormalConditions() {

        //when
        Object object = commentControllerPublic.getComment(comment.getId());
        CommentFullDto commentFullDto = (CommentFullDto) object;

        //then
        assertInstanceOf(CommentFullDto.class, object);
        assertEquals(commentFullDto.getId(), comment.getId());
        assertEquals(commentFullDto.getCommentator().getId(), user.getId());
        assertEquals(commentFullDto.getText(), "не понравилось");
    }

    @DirtiesContext
    @Test
    public void getComment_returnsCommentFullUpdateDto_underNormalConditions() {

        //given
        commentControllerPrivate.updateComment(CommentCreateDto.builder().text("жена сказала, что я не прав и мероприятие очень хорошее..").build(),
                comment.getId(), user.getId(), event.getId());

        //when
        Object object = commentControllerPublic.getComment(comment.getId());
        CommentFullUpdateDto commentFullUpdateDto = (CommentFullUpdateDto) object;

        //then
        assertInstanceOf(CommentFullUpdateDto.class, object);
        assertEquals(commentFullUpdateDto.getId(), comment.getId());
        assertEquals(commentFullUpdateDto.getCommentator().getId(), user.getId());
        assertEquals(commentFullUpdateDto.getText(), "жена сказала, что я не прав и мероприятие очень хорошее..");
        assertNotNull(commentFullUpdateDto.getCreatedOn());
        assertNotNull(commentFullUpdateDto.getUpdateOn());
    }

    @DirtiesContext
    @Test
    public void getComments_returnsCorrectCommentList_underNormalConditions() {

        //given
        commentControllerPrivate.updateComment(CommentCreateDto.builder().text("после изменения вернется CommentFullUpdateDto").build(),
                comment.getId(), user.getId(), event.getId());
        commentControllerPrivate.createComment(CommentCreateDto.builder().text("если не было корректировки, вернется CommentFullDto").build(), user.getId(), event.getId());

        //when
        List<Object> comments = commentControllerPublic.getComments(event.getId(), 0, 20);
        CommentFullUpdateDto commentFullUpdateDto = (CommentFullUpdateDto) comments.get(0);
        CommentFullDto commentFullDto = (CommentFullDto) comments.get(1);

        //then
        assertEquals(comments.size(), 2);
        assertInstanceOf(CommentFullUpdateDto.class, comments.get(0));
        assertInstanceOf(CommentFullDto.class, comments.get(1));

        assertEquals(commentFullUpdateDto.getId(), comment.getId());
        assertEquals(commentFullUpdateDto.getCommentator().getId(), user.getId());
        assertEquals(commentFullUpdateDto.getText(), "после изменения вернется CommentFullUpdateDto");

        assertEquals(commentFullDto.getId(), 2);
        assertEquals(commentFullDto.getCommentator().getId(), user.getId());
        assertEquals(commentFullDto.getText(), "если не было корректировки, вернется CommentFullDto");
    }
}
