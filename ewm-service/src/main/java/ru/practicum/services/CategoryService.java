package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.models.category.Category;
import ru.practicum.models.category.CategoryDto;
import ru.practicum.models.category.CategoryMapper;
import ru.practicum.repositories.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper mapper;
    private final CategoryRepository repository;

    public CategoryDto createCategory(CategoryDto dto) {

        log.info("Received a request to create a category " + dto);

        Category category = repository.save(mapper.dtoToObject(dto));

        return CategoryMapper.objectToDto(category);
    }

    public CategoryDto updateCategory(CategoryDto dto, int catId) {

        log.info("Received a request to update a category with an id " + catId);

        Category category = findCategoryById(catId);

        if (dto.getName().length() > 50){
            throw new BadRequestException("the maximum length of the name is 49 characters");
        }

        category.setName(dto.getName());

        return CategoryMapper.objectToDto(repository.save(category));
    }

    public void deleteCategory(int catId) {

        log.info("Received a request to delete a category with an id " + catId);

        findCategoryById(catId);

        repository.deleteById(catId);
    }

    public CategoryDto findDtoById(int catId) {

        log.info("Received a request to get a category with an  " + catId);

        Optional<Category> category = repository.findById(catId);

        if (category.isEmpty()) {
            throw new ObjectNotFoundException("There is no category with this id");
        } else {
            return CategoryMapper.objectToDto(category.get());
        }
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {

        log.info("Received a request to search for all categories for params: from {}, size {}", from, size);

        if (from < 0 || size <= 0) {
            throw new BadRequestException("the from parameter must be greater than or equal to 0; size is greater than 0");
        }

        return repository.findAll(PageRequest.of(from / size, size)).stream().map(CategoryMapper::objectToDto).collect(Collectors.toList());
    }

    public Category findCategoryById(int catId) {

        Optional<Category> category = repository.findById(catId);

        if (category.isEmpty()) {
            throw new ObjectNotFoundException("There is no category with this id");
        } else {
            return category.get();
        }
    }
}
