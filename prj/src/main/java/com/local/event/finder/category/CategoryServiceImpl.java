package com.local.event.finder.category;

import com.local.event.finder.model.entity.Category;
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
    public void create(CategoryRequestDto categoryDto) {
        Objects.requireNonNull(categoryDto, "Category cannot be null");

        String name = categoryDto.name().trim();
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category already exists");
        }

        Category category = new Category();
        category.setName(categoryDto.name().trim());

        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public List<CategoryResponseDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponseDto(category.getName()))
                .toList();
    }
}
