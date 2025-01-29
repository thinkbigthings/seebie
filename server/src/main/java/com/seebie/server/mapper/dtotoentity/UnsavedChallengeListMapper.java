package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.ChallengeDto;
import com.seebie.server.entity.User;
import com.seebie.server.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
public class UnsavedChallengeListMapper implements BiFunction<String, List<ChallengeDto>, List<com.seebie.server.entity.Challenge>> {

    private UserRepository userRepository;

    public UnsavedChallengeListMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<com.seebie.server.entity.Challenge> apply(String username, List<ChallengeDto> dtos) {
        User user = userRepository.findByPublicId(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + username));

        return dtos.stream()
                .map(dto -> toUnsavedEntity(user, dto))
                .toList();
    }

    public com.seebie.server.entity.Challenge toUnsavedEntity(String username, ChallengeDto dto) {
        return apply(username, List.of(dto)).getFirst();
    }

    private com.seebie.server.entity.Challenge toUnsavedEntity(User user, ChallengeDto dto) {
        return new com.seebie.server.entity.Challenge(dto.name(), dto.description(), dto.start(), dto.finish(), user);
    }

}
