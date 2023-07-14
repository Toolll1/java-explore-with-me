package ru.practicum.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.services.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryControllerAdmin {

    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto dto) {

        log.info("Received a request to create a category " + dto);

        return service.createCategory(dto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@RequestBody CategoryDto dto,
                                      @PathVariable Long catId) {

        log.info("Received a request to update a category with an id " + catId);

        return service.updateCategory(dto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {

        log.info("Received a request to delete a category with an id " + catId);

        service.deleteCategory(catId);
    }
}
