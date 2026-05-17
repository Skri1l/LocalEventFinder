package com.local.event.finder.event.category;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryInitializer implements CommandLineRunner {
    private static final List<String> CATEGORY_PRESET = List.of(
        "Music",
        "Workshops",
        "Meetups",
        "Active",
        "Food"
    );

    private CategoryRepository categoryRepository;


    @Override
    public void run(String... args) throws Exception {
        for (String categoryPreset : CategoryInitializer.CATEGORY_PRESET) {
            this.initializeCategory(categoryPreset);
        }
    }

    private void initializeCategory(String categoryName) {
        if (!categoryRepository.existsByName(categoryName)) {
            Category category = new Category();
            category.setName(categoryName);
            categoryRepository.save(category);
        }
    }
}
