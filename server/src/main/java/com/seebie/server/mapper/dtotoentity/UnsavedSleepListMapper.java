package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.SleepData;
import com.seebie.server.entity.SleepSession;
import com.seebie.server.entity.User;
import com.seebie.server.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;


@Component
public class UnsavedSleepListMapper implements BiFunction<String, List<SleepData>, List<SleepSession>> {

    private UserRepository userRepository;

    public UnsavedSleepListMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<SleepSession> apply(String username, List<SleepData> dtos) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + username));

        return dtos.stream()
                    .map(dto -> toUnsavedEntity(user, dto))
                    .toList();
    }

    public SleepSession toUnsavedEntity(String username, SleepData dto) {
        return apply(username, List.of(dto)).getFirst();
    }

    private SleepSession toUnsavedEntity(User user, SleepData dto) {
        SleepSession entity = new SleepSession();
        entity.setUser(user);
        entity.setSleepData(dto.minutesAwake(), dto.notes(), dto.startTime(), dto.stopTime(), dto.minutesAsleep(), dto.zoneId());
        return entity;
    }

}
