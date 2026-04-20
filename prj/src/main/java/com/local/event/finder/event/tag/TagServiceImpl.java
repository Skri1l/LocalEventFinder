package com.local.event.finder.event.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public void create(TagDto dto){
        Objects.requireNonNull(dto, "Tag can't be null");

        String name = dto.name().trim();

        if(name.isBlank()){
            throw new IllegalArgumentException("Tag name can't be blank");
        }

        if(tagRepository.existsByNameIgnoreCase(name)){
            throw new IllegalArgumentException("Tag already exists");
        }

        Tag tag = new Tag();
        tag.setName(name);
        tagRepository.save(tag);
    }

    @Override
    @Transactional
    public List<TagDto> getAll(){
        return tagRepository.findAll().stream()
                .map(tag -> new TagDto(tag.getName()))
                .toList();
    }
}
