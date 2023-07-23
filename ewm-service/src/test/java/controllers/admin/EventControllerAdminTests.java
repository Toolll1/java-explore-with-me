package controllers.admin;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.EwmServer;
import ru.practicum.controllers.admin.EventControllerAdmin;
import ru.practicum.controllers.priv.CommentControllerPrivate;
import ru.practicum.controllers.pub.CommentControllerPublic;
import ru.practicum.dto.*;
import ru.practicum.exceptions.ObjectNotFoundException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = EwmServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventControllerAdminTests {

    private final EventControllerAdmin eventControllerAdmin;
    private final CommentControllerPublic commentControllerPublic;
    private final CommentControllerPrivate commentControllerPrivate;

    @DirtiesContext
    @Test
    public void deleteComment_returnsNothing_underNormalConditions() {

        //given
        CommentFullDto comment =  commentControllerPrivate.createComment(
                CommentCreateDto.builder().text("не понравилось").build(), 1L, 1L);
        Long commentId = comment.getId();

        //when
        eventControllerAdmin.deleteComment(1L, comment.getId());

        //then
        assertThrows(ObjectNotFoundException.class, () -> commentControllerPublic.getComment(commentId));
        assertEquals(commentControllerPublic.getComments(1L, 0, 10), new ArrayList<>());
    }
}
