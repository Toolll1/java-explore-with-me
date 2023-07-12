package ru.practicum.models.category;

import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {

    public static CategoryDto objectToDto(Category category) {

        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category dtoToObject(CategoryDto dto) {

        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
