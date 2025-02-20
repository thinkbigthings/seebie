package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.SleepData;
import com.seebie.server.entity.SleepSession;
import com.seebie.server.entity.User;
import com.seebie.server.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;


@Component
public class UnsavedSleepListMapper implements BiFunction<UUID, List<SleepData>, List<SleepSession>> {

    private UserRepository userRepository;

    public UnsavedSleepListMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<SleepSession> apply(UUID publicId, List<SleepData> dtos) {

        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + publicId));

        return dtos.stream()
                    .map(dto -> toUnsavedEntity(user, dto))
                    .toList();
    }

    public SleepSession toUnsavedEntity(UUID publicId, SleepData dto) {
        return apply(publicId, List.of(dto)).getFirst();
    }

    private SleepSession toUnsavedEntity(User user, SleepData dto) {
        SleepSession entity = new SleepSession();
        entity.setUser(user);
        entity.setSleepData(dto.minutesAwake(), dto.notes(), dto.startTime(), dto.stopTime(), dto.minutesAsleep(), dto.zoneId());
        return entity;
    }

}
