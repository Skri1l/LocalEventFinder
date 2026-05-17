package com.local.event.finder.event.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public long create(CategoryRequestDto categoryDto) {
        Objects.requireNonNull(categoryDto, "Category cannot be null");

        String name = categoryDto.name().trim();
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category already exists");
        }

        Category category = new Category();
        category.setName(categoryDto.name().trim());

        return categoryRepository.save(category).getId();
    }

    @Override
    @Transactional
    public List<CategoryResponseDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponseDto(category.getId(), category.getName()))
                .toList();
    }
}
