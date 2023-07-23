package controllers.admin;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.EwmServer;
import ru.practicum.controllers.admin.BanControllerAdmin;
import ru.practicum.dto.*;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.models.Ban;
import ru.practicum.models.User;
import ru.practicum.repositories.BanRepository;
import ru.practicum.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EwmServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BanControllerAdminTests {

    private final BanControllerAdmin banControllerAdmin;
    private final BanRepository banRepository;
    private final UserRepository userRepository;
    User user;

    @BeforeEach
    void beforeEach() {

        user = userRepository.findById(1L).get();
    }

    @DirtiesContext
    @Test
    public void addCommentatorToBan_returnsTheCorrectBan_underNormalConditions() {

        //when
        BanDto ban = banControllerAdmin.addCommentatorToBan(1L, 2L);

        //then
        assertEquals(ban.getId(), 3);
        assertEquals(ban.getCommentator().getId(), user.getId());
    }

    @DirtiesContext
    @Test
    public void addCommentatorToBan_returnsException_underIncorrectUserId() {

        //then
        assertThrows(ObjectNotFoundException.class, () -> banControllerAdmin.addCommentatorToBan(999L, 2L));
    }

    @DirtiesContext
    @Test
    public void removeFromBan_returnsNothing_underNormalConditions() {

        //given
        banRepository.deleteAll();
        banControllerAdmin.addCommentatorToBan(1L, 2L);

        //when
        banControllerAdmin.removeFromBan(user.getId());

        //then
        assertEquals(banControllerAdmin.getBanList(0, 10), new ArrayList<>());
    }

    @DirtiesContext
    @Test
    public void getBanList_returnsCorrectList_underNormalConditions() {

        //given
        banRepository.deleteAll();
        BanDto ban = banControllerAdmin.addCommentatorToBan(1L, 2L);

        //when
        List<BanDto> banList = banControllerAdmin.getBanList(0, 10);

        //then
        assertEquals(banList.size(), 1);
        assertEquals(banList.get(0).getId(), ban.getId());
        assertEquals(banList.get(0).getCommentator().getId(), user.getId());
    }

    @DirtiesContext
    @Test
    public void getActive_returnsCorrectList_underNormalConditions() {

        //given
        BanDto ban = banControllerAdmin.addCommentatorToBan(1L, 2L);

        //when
        List<BanDto> banList = banControllerAdmin.getActive(0, 10);

        //then
        assertEquals(banList.size(), 1);
        assertEquals(banList.get(0).getId(), ban.getId());
        assertEquals(banList.get(0).getCommentator().getId(), user.getId());
    }

    @DirtiesContext
    @Test
    public void getOverdue_returnsCorrectList_underNormalConditions() {

        //given
        banRepository.deleteAll();
        LocalDateTime now = LocalDateTime.now();
        Ban banOverdue = banRepository.save(Ban.builder().createdOn(now.minusDays(2)).endOfBan(now.minusDays(1))
                .commentator(user).build());

        //when
        List<BanDto> banList = banControllerAdmin.getOverdue(0, 10);

        //then
        assertEquals(banList.size(), 1);
        assertEquals(banList.get(0).getId(), banOverdue.getId());
        assertEquals(banList.get(0).getCommentator().getId(), user.getId());
    }

    @DirtiesContext
    @Test
    public void removeOverdue_returnsCorrectList_underNormalConditions() {

        //given
        LocalDateTime now = LocalDateTime.now();
        banRepository.save(Ban.builder().createdOn(now.minusDays(2)).endOfBan(now.minusDays(1))
                .commentator(user).build());

        //when
        banControllerAdmin.removeOverdue();
        List<BanDto> banList = banControllerAdmin.getOverdue(0, 10);

        //then
        assertEquals(banList.size(), 0);
    }
}
