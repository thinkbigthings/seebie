package org.thinkbigthings.zdd.dto;

import java.time.Instant;

public record AlertConfig(SearchItemsInStores searches, boolean active, String lastUpdated) {

    public AlertConfig(SearchItemsInStores searches) {
        this(searches, true, Instant.now().toString());
    }

}
