package com.local.event.finder.event.tag;

import java.util.List;

public interface TagService {
    void create(TagDto dto);

    List<TagDto> getAll();
}
