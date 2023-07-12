package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.models.category.CategoryDto;
import ru.practicum.services.CategoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CategoryController {

    private final CategoryService service;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto dto) {

        return service.createCategory(dto);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto updateCategory(@RequestBody CategoryDto dto,
                                      @PathVariable int catId) {

        return service.updateCategory(dto, catId);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int catId) {

        service.deleteCategory(catId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return service.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto findDtoById(@PathVariable int catId) {

        return service.findDtoById(catId);
    }
}
