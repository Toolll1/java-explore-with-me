package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.models.category.Category;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.mappers.CategoryMapper;
import ru.practicum.repositories.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper mapper;
    private final CategoryRepository repository;

    public CategoryDto createCategory(CategoryDto dto) {

        Category category = repository.save(mapper.dtoToObject(dto));

        return CategoryMapper.objectToDto(category);
    }

    public CategoryDto updateCategory(CategoryDto dto, Long catId) {

        Category category = findCategoryById(catId);

        if (dto.getName().length() > 50) {
            log.info("method updateCategory - " +
                    "BadRequestException \"the maximum length of the name is 49 characters\"");
            throw new BadRequestException("the maximum length of the name is 49 characters");
        }

        category.setName(dto.getName());

        return CategoryMapper.objectToDto(repository.save(category));
    }

    public void deleteCategory(Long catId) {

        findCategoryById(catId);

        repository.deleteById(catId);
    }

    public CategoryDto findDtoById(Long catId) {

        Category category = findCategoryById(catId);

        return CategoryMapper.objectToDto(category);

    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {

        if (from < 0 || size <= 0) {
            log.info("method getCategories - " +
                    "BadRequestException \"the from parameter must be greater than or equal to 0; size is greater than 0\"");
            throw new BadRequestException("the from parameter must be greater than or equal to 0; size is greater than 0");
        }

        return repository.findAll(PageRequest.of(from / size, size)).stream().map(CategoryMapper::objectToDto).collect(Collectors.toList());
    }

    public Category findCategoryById(Long catId) {

        return repository.findById(catId).orElseThrow(() -> new ObjectNotFoundException("There is no category with this id"));
    }
}
