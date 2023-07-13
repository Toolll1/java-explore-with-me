package ru.practicum.controllers.publicControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.services.CategoryService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryControllerPublic {

    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {

        log.info("Received a request to search for all categories for params: from {}, size {}", from, size);

        return service.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto findDtoById(@PathVariable Long catId) {

        log.info("Received a request to get a category with an  " + catId);

        return service.findDtoById(catId);
    }
}
