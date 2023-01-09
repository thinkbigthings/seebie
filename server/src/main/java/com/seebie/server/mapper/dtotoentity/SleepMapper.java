package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.Sleep;
import com.seebie.server.dto.UserSleep;
import com.seebie.server.entity.SleepSession;
import com.seebie.server.entity.User;
import com.seebie.server.repository.TagRepository;
import com.seebie.server.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

@Component
public class SleepMapper implements Function<UserSleep, SleepSession> {

    private TagRepository tagRepository;
    private UserRepository userRepository;

    public SleepMapper(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SleepSession apply(UserSleep userSleep) {

        Sleep sleep = userSleep.sleep();
        String name = userSleep.username();

        User user = userRepository.findByUsername(name)
                                    .orElseThrow(() -> new IllegalArgumentException("user not found: " + name));

        SleepSession entity = new SleepSession();

        entity.setUser(user);
        entity.setDateAwakened(sleep.dateAwakened());
        entity.setMinutes(sleep.minutes());
        entity.setNotes(sleep.notes());
        entity.setOutOfBed(sleep.outOfBed());
        entity.setTags(sleep.tags().stream().map(text -> tagRepository.findByText(text)).collect(toSet()));

        return entity;
    }
}
