package com.seebie.server;

import com.seebie.server.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;


import java.util.Map;

import static com.seebie.server.test.data.TestData.newAppProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AppMetadataContributorTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final AppProperties properties = newAppProperties(30);
    private final AppMetadataContributor contributor = new AppMetadataContributor(userRepository, properties);

    @Test
    public void contributeTest() {

        long mockUserCount = 100L;
        when(userRepository.count()).thenReturn(mockUserCount);

        var builder = new Info.Builder();
        contributor.contribute(builder);
        var appInfo = (Map)builder.build().get("app");

        assertEquals(mockUserCount, appInfo.get("userCount"));
        assertEquals(properties.apiVersion(), appInfo.get("apiVersion"));
    }
}