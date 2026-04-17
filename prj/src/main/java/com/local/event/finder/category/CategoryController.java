package com.local.event.finder.category;

import com.local.event.finder.api.ApiResponseDto;
import com.local.event.finder.api.StatusResponseDto;
import com.local.event.finder.controller.EventController;
import com.local.event.finder.logging.AppLogger;
import com.local.event.finder.logging.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final static AppLogger log = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<StatusResponseDto>> create(@Valid @RequestBody CategoryRequestDto dto) {
        log.info("CategoryController:create");
        categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(new StatusResponseDto("OK")));
    }

    @GetMapping
    public ApiResponseDto<List<CategoryResponseDto>> getAll() {
        log.info("CategoryController:getAll");
        return new ApiResponseDto<>(categoryService.getAll());
    }
}
