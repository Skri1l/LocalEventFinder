package com.local.event.finder.event.tag;

import java.util.List;

public interface TagService {
    long create(TagDto dto);

    List<TagDto> getAll();
}
