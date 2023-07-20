package ru.practicum.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.BanDto;
import ru.practicum.services.BanService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class BanControllerAdmin {

    private final BanService service;

    @PostMapping("/ban/commentator/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BanDto addCommentatorToBan(@PathVariable Long userId,
                                      @RequestParam @Min(1) Long termInDays) {

        log.info("(Admin) Received to add a user with id  {} to the ban", userId);

        return service.addToBan(userId, termInDays);
    }

    @DeleteMapping("/ban/commentator/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromBan(@PathVariable Long userId) {

        log.info("(Admin) Request to remove a user with the id {} from the ban", userId);

        service.removeFromBanAdmin(userId);
    }

    @GetMapping("/ban")
    public List<BanDto> getBanList(@RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                   @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {

        log.info("(Admin) Request for a list of banned users");

        return service.getBanList(from, size);
    }

    @GetMapping("/ban/active")
    public List<BanDto> getActive(@RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {

        log.info("(Admin) Request for a list of banned users");

        return service.getActiveBanList(from, size);
    }

    @GetMapping("/ban/overdue")
    public List<BanDto> getOverdue(@RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                   @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {

        log.info("(Admin) Request for a list of banned users");

        return service.getOverdueBanList(from, size);
    }

    @DeleteMapping("/ban/overdue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOverdue() {

        log.info("(Admin) Request to remove all overdue from the ban");

        service.removeOverdueFromBan();
    }
}
