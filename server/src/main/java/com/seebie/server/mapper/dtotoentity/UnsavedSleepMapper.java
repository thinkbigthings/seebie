package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.SleepData;
import com.seebie.server.entity.SleepSession;
import com.seebie.server.entity.User;
import com.seebie.server.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class UnsavedSleepMapper implements BiFunction<String, SleepData, SleepSession> {

    private TagMapper tagMapper;
    private UserRepository userRepository;

    public UnsavedSleepMapper(TagMapper tagMapper, UserRepository userRepository) {
        this.tagMapper = tagMapper;
        this.userRepository = userRepository;
    }

    @Override
    public SleepSession apply(String username, SleepData dto) {

        User user = userRepository.findByUsername(username)
                                    .orElseThrow(() -> new IllegalArgumentException("user not found: " + username));

        SleepSession entity = new SleepSession();

        entity.setUser(user);

        entity.setSleepData(dto.dateAwakened(), dto.minutes(), dto.outOfBed(), dto.notes(), tagMapper.apply(dto.tags()),
                dto.startTime(), dto.stopTime());

        return entity;
    }

}
