package com.local.event.finder.event.category;

import java.util.List;

public interface CategoryService {

    long create(CategoryRequestDto dto);

    List<CategoryResponseDto> getAll();
}
