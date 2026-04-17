package com.local.event.finder.category;

import java.util.List;

public interface CategoryService {

    void create(CategoryRequestDto dto);

    List<CategoryResponseDto> getAll();
}
