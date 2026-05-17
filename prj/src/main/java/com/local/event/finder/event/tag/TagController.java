package com.local.event.finder.event.tag;

import com.local.event.finder.api.ApiResponseDto;
import com.local.event.finder.api.StatusCreateResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("tags")
public class TagController {

    private  final TagService tagService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<StatusCreateResponseDto>> create(@Valid @RequestBody TagDto dto) {
        long tagId = tagService.create(dto);
        return ResponseEntity.ok(new ApiResponseDto<>(new StatusCreateResponseDto(tagId, "OK")));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<TagDto>>> getAll() {
        return ResponseEntity.ok(new ApiResponseDto<>(tagService.getAll()));
    }
}
