package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.entity.Tag;
import com.seebie.server.repository.TagRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

@Component
public class TagMapper implements Function<Set<String>, Set<Tag>> {

    private TagRepository tagRepository;

    public TagMapper(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Set<Tag> apply(Set<String> tagText) {

        return tagText.stream()
                .map(text -> tagRepository.findByText(text))
                .collect(toSet());
    }
}
