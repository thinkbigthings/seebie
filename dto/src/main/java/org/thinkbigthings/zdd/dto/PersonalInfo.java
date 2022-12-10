package org.thinkbigthings.zdd.dto;

import java.util.Set;

public record PersonalInfo(String email,
                           String displayName,
                           Set<AddressRecord> addresses) {

}

